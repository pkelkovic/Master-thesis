package aggregator.service;

import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.entity.Status;

// Interface responsible for communication with Kafka broker
public interface KafkaService {

    // Function for publishing Sensor's Info to Kafka broker
    public void publishSensorInfo(SensorInfo info);

    // Function for publishing Sensor's Statistics to Kafka broker
    public void publishStats(SensorStats stats, String sensorType);

    // Function for publishing error messages to the main server
    public void publishError(String id, String msg);

    // Function for publishing Sensor's Status to the main server
    public void publishStatus(String id, Status status);
}
