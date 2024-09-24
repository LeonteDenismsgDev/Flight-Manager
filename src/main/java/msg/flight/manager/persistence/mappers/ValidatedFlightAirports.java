package msg.flight.manager.persistence.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import msg.flight.manager.persistence.models.airport.DBAirport;

@Data
@AllArgsConstructor
public class ValidatedFlightAirports {
    private DBAirport destination;
    private DBAirport departure;
}
