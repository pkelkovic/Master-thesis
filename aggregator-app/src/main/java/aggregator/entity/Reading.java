package aggregator.entity;

import java.time.Instant;

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
@Table(name = "READING")
public class Reading {
    // reading info:
    // identifier for this specific reading
    @Id
    @NotBlank
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Invalid UUID format detected in reading's id"
    )
    private String readingID;

    @NotNull
    private String readingUnit;

    @NotNull
    private double readingValue;

    @NotNull
    private Instant timestamp;
    
    // sensor info:
    // identifier for this specific sensor
    @NotBlank
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
        message = "Invalid UUID format detected in sensor's id"
    )
    private String sensorID;
}
