package msg.flight.manager.persistence.dtos.flights.flights;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightRecurrencePatternDTO {
    @NotNull(message = "Recurrence end is missing")
    private LocalDateTime recurrenceEnd;
    @NotNull(message = "recurrence pattern is missing")
    private String recurrencePattern;
}
