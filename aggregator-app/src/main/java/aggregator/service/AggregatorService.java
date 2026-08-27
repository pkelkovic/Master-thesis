package aggregator.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.entity.SensorStatsId;
import aggregator.entity.Status;

// The Main Aggregator Service
// - works PERIODICALLY, in CYCLES
// - gathers statistics for every sensor, adds them to the database
// - sends sensor's statistics to Kafka broker 
@Service
public class AggregatorService {
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private SensorStatsService statsService;
    @Autowired
    private DataResposibleService dataService;

    @Value("${PERIOD:100000}")
    private long period;

    @Value("${NO_ACTION_THRESHOLD:5}")
    private Integer threshold;

    @Value("${SPRING_APPLICATION_NAME:aggregator}")
    private String name;

    // Keep count of cycles when sensor had no data
    Map<String, Integer> noAction = new HashMap<>();


    // MAIN AGGREGATOR PERIODICAL FUNCTION
    @Scheduled(
        fixedRateString = "${PERIOD:300000}",
        initialDelayString = "${PERIOD:300000}"
    )
    public void mainAggregatorFunc() throws JsonProcessingException {
        // Determine the time window of the cycle
        Instant timeframeEnd = Instant.now();
        Instant timeframeStart = timeframeEnd.minusMillis(period);

        // Fetch the data for each sensor you have in database
        List<SensorInfo> sensorInfos = dataService.getAllSensors();

        // Initialize the count of cycles without action for every new sensor
        for (var sensor: sensorInfos) {
            if (!noAction.containsKey(sensor.getSensorID()))
                noAction.put(sensor.getSensorID(), 0);
        }

        // For each sensor: 
        // - get stats for this cycle
        // - save the stats to the database
        // - publish stats to Kafka topic
        // - track if sensor is not sending any data, and remove it after threshold non-active cycles
        for (SensorInfo sensor: sensorInfos) {
            SensorStatsId statsId = new SensorStatsId(sensor.getSensorID(), timeframeStart, timeframeEnd);
            SensorStats stats = statsService.getStats(statsId, sensor);

            String sensorID = sensor.getSensorID();
            String type = sensor.getType();

            // if sensor had no readings in this cycle, stats are null
            if (stats != null) {
                // save the stats to the database
                dataService.addNewStats(stats);

                // restart the No-action cycle
                noAction.put(sensorID, 0);
                // update the sensor's status (back) to active
                kafkaService.publishStatus(sensorID, Status.ACTIVE);

                // publish stats to the Kafka broker
                kafkaService.publishStats(stats, type);
            }
            else {
                int nonactiveCycles = noAction.get(sensorID);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                // sensor needs checking
                if (nonactiveCycles < threshold) {
                    // increase the count of non-active cycles for this sensor
                    nonactiveCycles++;
                    noAction.put(sensorID, nonactiveCycles);

                    // send the error message to the main server
                    kafkaService.publishError(sensorID, timestamp + " [ERROR in " + name + "] The " + type + " sensor with id " + sensorID + " had no readings in the past " + nonactiveCycles + " cycles. It will be removed after " + threshold + " non-active cycles.");
                    
                    // update the sensor's status to NEEDS_CHECK
                    kafkaService.publishStatus(sensorID, Status.NEEDS_CHECK);
                } 
                // sensor is proclaimed as NON-ACTIVE
                else {
                    // delete the sensor's active cycles count
                    noAction.remove(sensorID);

                    // remove the sensor from the database
                    dataService.removeSensor(sensor);

                    // send the error message to the main server
                    kafkaService.publishError(sensorID, timestamp + " [ERROR in " + name + "] The sensor with id " + sensorID + " had no readings in the past " + threshold + " cycles and is getting removed from the database.");
                    
                    // update the sensor's status to NOT_ACTIVE
                    kafkaService.publishStatus(sensorID, Status.NOT_ACTIVE);
                }
            }
        }

        // Reset all of the stats for new cycle        
        statsService.emptyAllReadings();
    }
}
