package aggregator.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.entity.Status;
import aggregator.service.KafkaService;
import lombok.NonNull;

@Service
public class KafkaServiceImpl implements KafkaService{

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Configurable value of Kafka's topic for publishing sensor's statistics
    @Value("${KAFKA_STATS_TOPIC:sensor.stats.}")
    private String kafkaStatsTopic;
    // Configurable value of Kafka's topic for publishing sensor's information
    @Value("${KAFKA_SENSOR_TOPIC:sensor.info}")
    private String kafkaInfoTopic;
    // Configurable value of Kafka's topic for publishing error messages
    @Value("${KAFKA_ERROR_TOPIC:sensor.error.}")
    private String kafkaErrorTopic;
    // Configurable value of Kafka's topic for publishing sensor's status
    @Value("${KAFKA_STATUS_TOPIC:sensor.status.}")
    private String kafkaStatusTopic;
    
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);

    private void sendMessage(@NonNull String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void publishSensorInfo(SensorInfo info) {
        try {
            sendMessage(kafkaInfoTopic, objectMapper.writeValueAsString(info));
            log.info("[KAFKA] Sensor info message sent to the Kafka broker, on topic " + kafkaInfoTopic);
        } catch (JsonProcessingException e) {
            log.error("[KAFKA] An error occured while trying to map SensorInfo object to a JSON object.");
        }
    }

    public void publishStats(SensorStats stats, String sensorType) {
        try {
            var topic = kafkaStatsTopic + sensorType;
            sendMessage(topic, objectMapper.writeValueAsString(stats));
            log.info("[KAFKA] Sensor's statistics message sent to the Kafka broker, on topic " + topic);
        } catch (JsonProcessingException e) {
            log.error("[KAFKA] An error occured while trying to map SensorStats object to a JSON object.");
        }
    }  

    public void publishError(String id, String msg) {
        sendMessage(kafkaErrorTopic + id, msg);
        log.info("[KAFKA] Error message published on topic " +  kafkaErrorTopic + " with message: \n" + msg + "\n\n");
    }

    public void publishStatus(String id, Status status) {
        sendMessage(kafkaStatusTopic + id, status.toString());
        log.info("[KAFKA] Sensor's [ID: " + id +" ] status message published on topic " +  kafkaStatusTopic + " with status: \n" + status.toString() + "\n\n");
    }
}