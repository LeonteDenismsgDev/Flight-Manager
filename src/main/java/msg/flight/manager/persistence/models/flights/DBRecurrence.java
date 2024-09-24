package msg.flight.manager.persistence.models.flights;

import lombok.*;
import msg.flight.manager.persistence.dtos.flights.flights.FlightAirportDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightUserDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@CompoundIndexes({
        @CompoundIndex(name = "recurrence_period_index", def = "{'startRecursivePeriod': 1, 'endRecursiveTimePeriod': 1}")
})
public class DBRecurrence {
    @Id
    private String recursionId;
    private LocalDateTime startRecursivePeriod;
    private LocalDateTime endRecursivePeriod;
    private LocalDateTime scheduledDepartureTime;
    private LocalDateTime scheduledArrivalTime;
    private FlightAirportDTO destination;
    private FlightAirportDTO departure;
    private String template;
    private Map<String, Object> templateAttributes;
    private String type;
    private String recurrencePattern;
    private FlightUserDTO editor;
    private String company;
}
