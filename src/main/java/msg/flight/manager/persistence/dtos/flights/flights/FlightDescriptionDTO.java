package msg.flight.manager.persistence.dtos.flights.flights;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FlightDescriptionDTO {
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private LocalDateTime scheduledArrivalTime;
    private LocalDateTime scheduledDepartureTime;
    private FlightAirportDTO destination;
    private FlightAirportDTO departure;
    private FlightPlaneDTO flightPlaneDTO;
    private List<FlightUserDTO> crew;
    private Map<String, Object> templateAttributes;
    private String state;
    private String recurrenceId;
    private Integer recurrenceNumber;
    private FlightUserDTO editor;
    private String template;
    private String company;
}
