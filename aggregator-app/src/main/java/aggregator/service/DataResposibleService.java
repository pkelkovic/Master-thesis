package aggregator.service;

import java.util.List;

import aggregator.entity.Reading;
import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;


// Interface responsible for communication with database
public interface DataResposibleService {
    // Sensor Info:
    // Function for adding new sensor to the database
    public void addNewSensor(SensorInfo sensorInfo);
    
    // Function for deleting sensor from the database
    public void removeSensor(SensorInfo sensor);

    // Function that returns list of all saved sensor's infos
    public List<SensorInfo> getAllSensors();

    // Function for checking if sensor with provided id exists in the database
    public boolean existsById(String sensorId);


    // Reading:
    // Function for adding new reading to the database
    public void addNewReading(Reading reading);


    // Sensor Stats:
    // Function for adding new stats to the database
    public void addNewStats(SensorStats stats);
}
