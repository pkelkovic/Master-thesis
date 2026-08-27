package aggregator.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "SENSOR_STATS",
    indexes = {
        @Index(name = "idx_sensor_time", columnList = "sensor_id, timeframe_start, timeframe_end")
    }
)
public class SensorStats {
    // id for sensor stats = combination of sensor's id and timeframe of calculation
    @EmbeddedId
    @NotNull
    private SensorStatsId statsId;

    @NotBlank
    private String unit;

    // simple data analysis
    private double average;
    private double stdDev;

    // for spike detection
    private double min;
    private double max;

    // number of readings used
    private long count; 
}
