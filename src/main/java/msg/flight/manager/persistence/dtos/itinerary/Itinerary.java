package msg.flight.manager.persistence.dtos.itinerary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.utils.TimeHelper;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {

    private String ID;

    @NotNull(message =  "departure should not  be null")
    @NotBlank(message = "departure should not be blank")
    private String departure;

    @NotNull(message =  "arrival should not  be null")
    @NotBlank(message = "arrival should not be blank")
    private String arrival;

    @NotNull(message =  "departure time should not  be null")
    @NotBlank(message = "departure time should not be blank")
    private TimeHelper departureTime;

    @NotNull(message =  "arrival time should not  be null")
    @NotBlank(message = "arrival time should not be blank")
    private TimeHelper arrivalTime;

    private short lateDepartureMinutes = 0;

    private short lateArrivalMinutes = 0;

    @NotNull(message =  "Flight Number should not  be null")
    @NotBlank(message = "Flight Number should not be blank")
    private String flightNumber;

    private String crewNumber;
}
