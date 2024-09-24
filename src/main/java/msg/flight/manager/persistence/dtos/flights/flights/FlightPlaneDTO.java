package msg.flight.manager.persistence.dtos.flights.flights;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FlightPlaneDTO {
    private String registrationNumber;
    private String  model;
}
