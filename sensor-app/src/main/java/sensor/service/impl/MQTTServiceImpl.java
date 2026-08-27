package sensor.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import sensor.service.MQTTService;

import org.springframework.beans.factory.annotation.Value;

@Service
public class MQTTServiceImpl implements MQTTService{
    // List of broker's IP adresses, seperated by a comma
    @Value("${MQTT_BROKERS:tcp://mosquitto-0:1883}")
    private String brokersString;

    @Value("${DEFAULT_BROKER:tcp://mosquitto-0:1883}")
    private String defaultBrokerString;

    @Value("${MQTT_CLIENT_ID:my_sensor}")
    private String clientId;

    // for setting messages to retained mode
    @Value("${MQTT_INFO_TOPIC:sensor/information/}")
    private String infoTopic;

    private MqttClient client;

    @Autowired
    private ObjectMapper objectMapper; // for converting reading to JSON format

    
    private static final Logger log = LoggerFactory.getLogger(MQTTServiceImpl.class);


    @PostConstruct
    public void connectClient() {
        try {
            List<String> brokerURIs = Arrays.asList(brokersString.split(","));
            String selectedBroker = brokerURIs.get(
                ThreadLocalRandom.current().nextInt(brokerURIs.size())
            );
            // default broker if something went wrong
            if (selectedBroker == null || selectedBroker.isEmpty()) selectedBroker = defaultBrokerString;

            log.info("[CONNECTION] Connecting to MQTT broker: " + selectedBroker);

            this.client = new MqttClient(
                selectedBroker,
                resolveClientId(),
                new MemoryPersistence()
            );
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);    
            log.info("[CONNECTION] MQTT sucessfully connected to broker!");    
        } catch (MqttException e) {
            log.error("[CONNECTION] MQTT client failed to connect to broker.");
            e.printStackTrace(); 
        }
    }


    @Override
    public void publishMessage(String topic, Object message) {
        try {
            ensureConnected();
            // check connection
            if (client == null || !client.isConnected()) {
                log.warn("[CONNECTION] MQTT client isn't connected yet, so the message cannot be published.");
                return;
            }
            MqttMessage msg = new MqttMessage(toJson(message).getBytes());
            msg.setQos(1); // message should be delivered at least once
            // info messages should be retained --> it's very important that aggregators get them
            if (topic.startsWith(infoTopic)) {
                msg.setRetained(true); 
            }
            client.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    } 

    private void ensureConnected() throws MqttException {
        if (client == null || !client.isConnected()) {
            client.reconnect();
        }
    }
    

    public String toJson(Object msg) {
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String resolveClientId() {
        if (clientId == null || clientId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return clientId;
    }

}
