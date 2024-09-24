package msg.flight.manager.persistence.dtos.flights.flights;

import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class FlightAirportDTO {
    private String icao;
    private String airportName;
}
