package sensor.entity;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reading {
    // reading info:
    // identifier for this specific reading
    private String readingID;

    private String readingUnit;

    private double readingValue;

    private Instant timestamp;
    
    // sensor info:
    // identifier for this specific sensor
    private String sensorID;
}
