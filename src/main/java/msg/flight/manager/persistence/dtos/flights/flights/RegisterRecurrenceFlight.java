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
public class RegisterRecurrenceFlight extends ValidationFlightDTO {
    @NotNull(message = "Recurrence id  should  not be null")
    private String recurrenceId;
    @NotNull(message = "Recurrence number  should  not be null")
    private Integer recurrenceNumber;
    @NotNull(message = "Arrival time should not be null")
    private LocalDateTime arrivalTime;
    @NotNull(message = "Departure time should not be null")
    private LocalDateTime departureTime;

    @Builder
    public RegisterRecurrenceFlight(@NotNull LocalDateTime scheduledDepartureTime, @NotNull LocalDateTime scheduledArrivalTime,
                                    FlightPlaneDTO plane, Set<FlightUserDTO> crew, @NotNull @NotEmpty String destination, @NotNull @NotNull String departure,
                                    @NotNull @NotEmpty String template, @NotNull JsonNode templateAttributes,
                                    @NotNull String recurrenceId, @NotNull Integer recurrenceNumber) {
        super(scheduledDepartureTime, scheduledArrivalTime, plane, crew, destination, departure, template, templateAttributes);
        this.recurrenceId = recurrenceId;
        this.recurrenceNumber = recurrenceNumber;
    }
}
