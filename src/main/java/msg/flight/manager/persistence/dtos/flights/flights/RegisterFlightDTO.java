package msg.flight.manager.persistence.dtos.flights.flights;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RegisterFlightDTO extends ValidationFlightDTO {
    private FlightRecurrencePatternDTO recursivePattern;

    @Builder
    public RegisterFlightDTO(@NotNull LocalDateTime scheduledDepartureTime, @NotNull LocalDateTime scheduledArrivalTime,
                             FlightPlaneDTO plane, Set<FlightUserDTO> crew, @NotNull @NotEmpty String destination,
                             @NotNull @NotNull String departure, @NotNull @NotEmpty String template,
                             @NotNull JsonNode templateAttributes, FlightRecurrencePatternDTO  recursivePattern) {
        super(scheduledDepartureTime, scheduledArrivalTime, plane, crew, destination, departure, template, templateAttributes);
        this.recursivePattern = recursivePattern;
    }
}
