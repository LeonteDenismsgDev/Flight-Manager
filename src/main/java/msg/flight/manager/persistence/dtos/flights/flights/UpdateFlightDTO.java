package msg.flight.manager.persistence.dtos.flights.flights;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UpdateFlightDTO extends ValidationFlightDTO {
    @NotNull(message = "Departure time should not be null")
    private LocalDateTime departureTime;
    @NotNull(message = "Arrival time should not be null")
    private LocalDateTime arrivalTime;

    @Builder
    public UpdateFlightDTO(@NotNull LocalDateTime scheduledDepartureTime, @NotNull LocalDateTime scheduledArrivalTime,
                           FlightPlaneDTO plane, Set<FlightUserDTO> crew, @NotNull @NotEmpty String destination,
                           @NotNull @NotNull String departure, @NotNull @NotEmpty String template,
                           @NotNull JsonNode templateAttributes, @NotNull LocalDateTime departureTime,
                           @NotNull LocalDateTime arrivalTime, @NotNull String state) {
        super(scheduledDepartureTime, scheduledArrivalTime, plane, crew, destination, departure, template, templateAttributes);
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}
