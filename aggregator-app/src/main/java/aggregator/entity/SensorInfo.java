package aggregator.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SENSOR_INFORMATION")
public class SensorInfo {

    // identifier for this specific sensor - has to be in UUID format
    @Id
    @NotBlank
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Invalid UUID format detected in sensor's id"
    )
    private String sensorID;

    // temperature, humidity, weight,... sensor
    @NotBlank
    private String type;

    // C, %, kg, ...
    @NotBlank
    private String readingUnit;

    // manufacturing model's name, usefull for repearing
    @NotBlank
    private String model;

    // Number of zone, rack and shelf that sensor is on
    @Embedded
    @NotNull
    private Location location;
}