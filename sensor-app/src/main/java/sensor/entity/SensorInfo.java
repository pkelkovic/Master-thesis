package sensor.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorInfo {

    // identifier for this specific sensor
    @NotBlank
    private String sensorID;

    // temperature, humidity, weight sensor
    @NotBlank
    private String type;

    // C, %, kg, ...
    @NotBlank
    private String readingUnit;

    // manufacturing model's name, useful for repearing
    @NotBlank
    private String model;

    // Number of zone, rack and shelf that sensor is on
    @NotNull
    private Location location;
}