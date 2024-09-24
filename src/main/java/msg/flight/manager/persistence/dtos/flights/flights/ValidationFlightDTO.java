package msg.flight.manager.persistence.dtos.flights.flights;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ValidationFlightDTO {
    @NotNull(message = "The flight is missing a scheduled departure time")
    private LocalDateTime scheduledDepartureTime;
    @NotNull(message = "The flight is missing a scheduled arrival time")
    private LocalDateTime scheduledArrivalTime;
    private FlightPlaneDTO plane;
    private Set<FlightUserDTO> crew;
    @NotNull(message = "Destination airport is missing")
    @NotEmpty(message = "Destination airport is missing")
    private String destination;
    @NotNull(message = "Departure airport is  missing")
    @NotNull(message = "Departure airport is missing")
    private String departure;
    @NotNull(message = "Flight template is missing")
    @NotEmpty(message = "Flight  template is missing")
    private String template;
    @NotNull(message = "Template attributes are required")
    private JsonNode templateAttributes;
}