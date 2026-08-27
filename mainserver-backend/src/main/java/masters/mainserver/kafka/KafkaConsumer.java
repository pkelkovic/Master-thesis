package masters.mainserver.kafka;


import org.springframework.stereotype.Component;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import masters.mainserver.service.KafkaService;


@Component
public class KafkaConsumer {

    @Autowired
    private final KafkaService service;

    public KafkaConsumer(KafkaService service) {
        this.service = service;
    }

    @KafkaListener(topicPattern = "${KAFKA_TOPIC_PATTERN}")
    public void consume(
        String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        service.processMessage(message, topic);
    }
}