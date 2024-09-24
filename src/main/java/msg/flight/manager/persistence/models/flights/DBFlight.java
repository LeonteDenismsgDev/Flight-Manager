package msg.flight.manager.persistence.models.flights;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import msg.flight.manager.persistence.dtos.flights.flights.FlightAirportDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightPlaneDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightUserDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "flights")
@CompoundIndexes({
        @CompoundIndex(name = "recurrence_period_index", def = "{'arrivalTime': 1, 'departureTime': 1, 'company': 1}")
})
public class DBFlight {
    public static final String FLIGHT = "flight";
    @Id
    private String flightNumber;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private LocalDateTime scheduledArrivalTime;
    private LocalDateTime scheduledDepartureTime;
    private FlightAirportDTO destination;
    private FlightAirportDTO departure;
    private String template;
    private Map<String, Object> templateAttributes;
    private String type;
    private String state;
    private String recursionId;
    private FlightPlaneDTO plane;
    private List<FlightUserDTO> crew;
    private String company;
    private FlightUserDTO editor;
    private Integer recurrenceNumber;
    private Boolean generated;

    @Builder(builderClassName = "DBFlightBuilder")
    public DBFlight(@NotNull LocalDateTime arrivalTime, @NotNull LocalDateTime departureTime,
                    @NotNull LocalDateTime scheduledArrivalTime, @NotNull LocalDateTime scheduledDepartureTime,
                    @NotNull FlightAirportDTO destination, @NotNull FlightAirportDTO departure, @NotNull String template,
                    @NotNull Map<String, Object> templateAttributes, @NotNull String state, String recursionId,
                    @NotNull FlightPlaneDTO plane, @NotNull List<FlightUserDTO> crew, @NotNull String company, @NotNull FlightUserDTO editor,
                    Integer recurrenceNumber, @NotNull Boolean generated) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.destination = destination;
        this.departure = departure;
        this.template = template;
        this.templateAttributes = templateAttributes;
        this.state = state;
        this.recursionId = recursionId;
        this.company = company;
        this.editor = editor;
        this.plane = plane;
        this.crew = crew;
        this.recurrenceNumber = recurrenceNumber;
        this.generated = generated;
        this.type = FLIGHT;
    }


    private String getAuditFlightType() {
        return String.format("%s_%s", FLIGHT, LocalDateTime.now());
    }

    public DBFlight getAuditEntry() {
        DBFlight auditFlight = DBFlight.builder()
                .arrivalTime(this.arrivalTime)
                .departureTime(this.departureTime)
                .scheduledArrivalTime(this.scheduledArrivalTime)
                .scheduledDepartureTime(this.scheduledDepartureTime)
                .destination(this.destination)
                .departure(this.departure)
                .template(this.template)
                .templateAttributes(this.templateAttributes)
                .state(this.state)
                .recursionId(this.recursionId)
                .company(this.company)
                .editor(this.editor)
                .crew(this.crew)
                .plane(this.plane)
                .recurrenceNumber(this.recurrenceNumber)
                .generated(this.generated)
                .build();
        auditFlight.setType(getAuditFlightType());
        return auditFlight;
    }
}
