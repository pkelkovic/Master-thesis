package masters.mainserver.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import masters.mainserver.entity.SensorInfo;
import masters.mainserver.entity.SensorStats;
import masters.mainserver.entity.Status;
import masters.mainserver.service.DataService;
import masters.mainserver.service.KafkaService;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaServiceImpl implements KafkaService{

    @Autowired
    private DataService dataService;

    // Configurable value of Kafka's topic for publishing sensor's statistics
    @Value("${KAFKA_STATS_TOPIC:sensor.stats}")
    private String kafkaStatsTopic;
    // Configurable value of Kafka's topic for publishing sensor's information
    @Value("${KAFKA_SENSOR_TOPIC:sensor.info}")
    private String kafkaInfoTopic;
    // Configurable value of Kafka's topic for publishing error messages
    @Value("${KAFKA_ERROR_TOPIC:sensor.error}")
    private String kafkaErrorTopic;
    // Configurable value of Kafka's topic for publishing sensor's status
    @Value("${KAFKA_STATUS_TOPIC:sensor.status.}")
    private String kafkaStatusTopic;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);

    // Get the message, map it to the correct object, save it to database
    public void processMessage(String message, String topic) {
        // Received sensor's stats
        if (topic.startsWith(kafkaStatsTopic)) {
            SensorStats stats = objectMapper.readValue(message, SensorStats.class);
            log.info("[KAFKA] Received a Sensor Stats message on topic " + topic + " from sensor with ID: " + stats.getStatsId().getSensorId());
            log.info("[RECEIVED SENSOR STATS] \nAverage: " + stats.getAverage() +" "+stats.getUnit()+ "\nStandard deviation: " + stats.getStdDev()+" "+stats.getUnit() + "\nMin: "+stats.getMin()+" "+stats.getUnit()+"\nMax:"+stats.getMax()+" "+stats.getUnit()+"\nCount: "+stats.getCount()+"\n");
            dataService.addSensorStats(stats);
        } 
        // Received sensor's info
        else if (topic.startsWith(kafkaInfoTopic)) {
            SensorInfo info = objectMapper.readValue(message, SensorInfo.class);
            log.info("[KAFKA] Received a Sensor Info message from sensor with ID: " + info.getSensorID());
            dataService.addSensorInfo(info);
        }
        // Received error message
        else if (topic.startsWith(kafkaErrorTopic)) {
            log.info("[KAFKA] Received an ERROR message: \n" + message + "\n");
            dataService.addErrorMessage(message);
        }
        // Received a sensor's status update
        else if (topic.startsWith(kafkaStatusTopic)) {
            String id = topic.substring(kafkaStatusTopic.length());
            log.info("[KAFKA] Received an STATUS UPDATE for sensor:\nID: " + id + "\nSTATUS: " + message + "\n");
            dataService.updateSensorStatus(id, Status.valueOf(message));
        }
    }
}
