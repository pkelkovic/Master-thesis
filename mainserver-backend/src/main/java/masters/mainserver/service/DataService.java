package masters.mainserver.service;

import java.util.List;

import masters.mainserver.entity.SensorInfo;
import masters.mainserver.entity.SensorStats;
import masters.mainserver.entity.Status;

public interface DataService {
    // Getting data
    public List<SensorInfo> getSensorInfo();
    public List<SensorStats> getSensorStats(String sensorID);
    public List<String> getErrorMessages(); // from newest to oldest
    public Status getSensorStatus(String id);

    // Adding data
    public void addSensorStats(SensorStats stats);
    public void addSensorInfo(SensorInfo info);
    public void addErrorMessage(String msg);
    public void updateSensorStatus(String id, Status status);
}
