package aggregator.MQTT;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import aggregator.entity.*;
import aggregator.service.DataResposibleService;
import aggregator.service.KafkaService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MQTTService {
    // Modifiable data from config map YAML file:
    @Value("${MQTT_BROKER_URI:tcp://mosquitto-0.mosquitto:1883}")
    private String brokerURI;

    @Value("${MQTT_SENSOR_TOPIC:sensor/data/#}")
    private String readingTopic;

    @Value("${MQTT_INFO_TOPIC:sensor/information/#}")
    private String sensorInfoTopic;

    @Value("${MQTT_CLIENT_ID:aggregator}")
    private String clientId;

    private MqttClient client;

    private static final Logger log = LoggerFactory.getLogger(MQTTService.class);

    // Main service that is responsible for data processing and database conncetion
    @Autowired
    private DataResposibleService dataService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${MQTT_RETRY_DELAY_MS:5000}")
    private long retryDelayMs;

    @PostConstruct
    public void connectClient() {
        try {
            this.client = new MqttClient(brokerURI, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            connectWithRetry(options);
            log.info("[MQTT] Established a conncetion with MQTT broker " + brokerURI);
            client.subscribe(sensorInfoTopic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String value = new String(message.getPayload(), StandardCharsets.UTF_8);
                    SensorInfo sensorInfo = objectMapper.readValue(value, SensorInfo.class);
                    log.info("[MQTT] Received a sensor info: " + sensorInfo);
                    // basic data validation is done through database
                    if (sensorInfo != null) {
                        // adding new sensor is done through the main service
                        dataService.addNewSensor(sensorInfo);
                        kafkaService.publishSensorInfo(sensorInfo);
                        log.info("[SENSOR_INFO] Received information about sensor with id: {}", sensorInfo.getSensorID());
                    }
                    else {
                        log.error("[SENSOR_INFO] Invalid sensor information received.");
                    }
                }
            });

            log.info("[MQTT] Sucessfully subscribed to topic " + sensorInfoTopic);
    
            client.subscribe(readingTopic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String value = new String(message.getPayload(), StandardCharsets.UTF_8);
                    Reading reading = objectMapper.readValue(value, Reading.class);
                    if (reading != null) {
                        String sensorId = reading.getSensorID();
                        if (sensorId != null && dataService.existsById(sensorId)) {
                            // adding new reading is also done through the same service layer
                            dataService.addNewReading(reading);
                            
                            log.info("[READING] Received reading (" + reading.getReadingValue() + " " +  reading.getReadingUnit() + ") from sensor with id: " + sensorId);
                        } else {
                            log.error("[READING] Received reading from unknown sensor: " + sensorId);
                        }
                    } 

                }
            });    
            log.info("[MQTT] Sucessfully subscribed to topic " + readingTopic);  
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

// function for reconnecting with MQTT broker if inital connection fails
    private void connectWithRetry(MqttConnectOptions options) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                client.connect(options);
                return;
            } catch (MqttException e) {
                log.warn("[MQTT] Connection attempt {} to {} failed ({}), retrying in {} ms",
                        attempt, brokerURI, e.getMessage(), retryDelayMs);
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

}
