package msg.flight.manager.persistence.dtos.itinerary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.plane.Plane;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {


    @NotNull(message =  "ID should not  be null")
    @NotBlank(message = "ID should not be blank")
    private String ID;

    @NotNull(message =  "departure should not  be null")
    @NotBlank(message = "departure should not be blank")
    private String departure;

    @NotNull(message =  "arrival should not  be null")
    @NotBlank(message = "arrival should not be blank")
    private String arrival;

    @NotNull(message =  "departure time should not  be null")
    @NotBlank(message = "departure time should not be blank")
    private long departureTime;

    @NotNull(message =  "arrival time should not  be null")
    @NotBlank(message = "arrival time should not be blank")
    private long arrivalTime;

    @NotNull(message =  "plane should not  be null")
    @NotBlank(message = "plane should not be blank")
    private Plane plane;
}
