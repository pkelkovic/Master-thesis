package sensor.service;

public interface MQTTService {
    // Function for publishing the message to the topic on MQTT broker
    public void publishMessage(String topic, Object message);
}
