package aggregator.service;

import aggregator.entity.Reading;
import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.entity.SensorStatsId;

// Simple interface of the service with most business logic - focus on stats

public interface SensorStatsService {
    // Function for adding new sensor
    public void addNewSensor(String sensorId);

    // Function for adding new reading to sensor's list
    public void addNewReading(Reading reading);

    // Function for reseting the collection system
    public void emptyAllReadings();

    // Functions for getting stats about sensors 
    // If sensor didn't have any readings in this cycle, it returns null
    public SensorStats getStats(SensorStatsId statsId, SensorInfo sensor);

}
