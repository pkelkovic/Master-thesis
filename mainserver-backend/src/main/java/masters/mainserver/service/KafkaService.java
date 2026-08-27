package masters.mainserver.service;

public interface KafkaService {

    // Function that saves data received from Kafka broker to the database
    public void processMessage(String message, String topic);

}
