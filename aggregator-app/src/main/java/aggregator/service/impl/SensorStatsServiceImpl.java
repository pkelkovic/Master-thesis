package aggregator.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aggregator.MQTT.MQTTService;
import aggregator.entity.Reading;
import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.entity.SensorStatsId;
import aggregator.service.SensorStatsService;
import lombok.NonNull;

@Service
public class SensorStatsServiceImpl implements SensorStatsService {
    // Key: sensor id
    // Value: list of sensor's readings IN THIS CYCLE
    private Map<String, List<Reading>> readingsBySensor = new HashMap<>();

    // Map for tracking sensor's stats -> sum, count, min, max
    // Key: sensor id
    // Value: sensor's stats saved in a List
    private int sumIdx = 0, countIdx = 1, minIdx = 2, maxIdx = 3;
    private Map<String, List<Double>> sensorStats = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(MQTTService.class);

    public void addNewSensor(@NonNull String sensorId) {
        readingsBySensor.put(sensorId, new ArrayList<Reading>());

        // Setting default values for tracking sensor's stats
        List<Double> values = new ArrayList<>(Collections.nCopies(4, 0.0d));
        values.set(minIdx, Double.MAX_VALUE);
        values.set(maxIdx, Double.MIN_VALUE);
        sensorStats.put(sensorId, values);
    }


    public void addNewReading(@NonNull Reading reading) {
        // Add the reading to a list of sensor's readings from this cycle
        String sensorId = reading.getSensorID();
        readingsBySensor.get(sensorId).add(reading);

        // Real-time stats tracking
        double value = reading.getReadingValue();
        var stats = sensorStats.get(sensorId);

        double sum = stats.get(sumIdx) + value;
        stats.set(sumIdx, sum);

        double count = stats.get(countIdx) + 1;
        stats.set(countIdx, count);

        double min = stats.get(minIdx);
        if (value < min) stats.set(minIdx, value);
        
        double max = stats.get(maxIdx);
        if (value > max) stats.set(maxIdx, value);

        return;
    }

    public void emptyAllReadings() {
        for (var id: readingsBySensor.keySet()) {
            readingsBySensor.get(id).clear();

            // Reseting stats to default values
            sensorStats.get(id).set(sumIdx, 0.0d);
            sensorStats.get(id).set(countIdx, 0.0d);
            sensorStats.get(id).set(minIdx, Double.MAX_VALUE);
            sensorStats.get(id).set(maxIdx, Double.MIN_VALUE);
        }
    }


    public SensorStats getStats(SensorStatsId statsId, SensorInfo sensor) {
        String sensorId = sensor.getSensorID();
        double average = sensorStats.get(sensorId).get(sumIdx) / sensorStats.get(sensorId).get(countIdx);

        int count = (sensorStats.get(sensorId).get(countIdx)).intValue();

        if (count > 0) {
            double stddev = 0.0d;
            var readings = readingsBySensor.get(sensorId);
            for(var r: readings) {
                stddev += Math.pow((r.getReadingValue() - average), 2.0);
            }
            stddev /= count;
            stddev = Math.sqrt(stddev);

            double min = sensorStats.get(sensorId).get(minIdx);
            double max = sensorStats.get(sensorId).get(maxIdx);

            String unit = sensor.getReadingUnit();

            SensorStats stats = new SensorStats(statsId, unit, average, stddev, min, max, count);

            log.info("[SENSOR STATS] " +  sensor.getType() + " sensor's id: " + sensorId + "\nAverage: " + average +" "+unit+ "\nStandard deviation: " + stddev+" "+unit + "\nMin: "+min+" "+unit+"\nMax:"+max+" "+unit+"\nCount: "+count+" "+"\n");

            return stats;
        } else {
            log.info("[SENSOR STATS] !!!" +  sensor.getType() + " sensor's id: " + sensorId + " had no readings in this cycle.");
            return null;
        }
    }

}