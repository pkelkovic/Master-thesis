package masters.mainserver.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class ConfigProperties {

    // topics that main server must be subscribed to
    private List<String> kafkaTopics;
}