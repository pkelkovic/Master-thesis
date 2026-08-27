package masters.mainserver.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorStatsId implements Serializable {
    // key for storing sensor's stats --> combination of sensor id and the calculating timeframe
    @NotBlank
    @Column(name = "sensor_id")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Invalid UUID format detected in sensor's id"
    )
    private String sensorId;

    // Start and end point of the calculating timeframe
    @NotNull
    @Column(name = "timeframe_start")
    private Instant timeframeStart;

    @NotNull
    @Column(name = "timeframe_end")
    private Instant timeframeEnd;
}
