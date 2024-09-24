package msg.flight.manager.persistence.dtos.flights.flights;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FlightUserDTO {
    private String username;
    private String  firstName;
    private String lastName;
    private Map<String,String> contactData;
}
