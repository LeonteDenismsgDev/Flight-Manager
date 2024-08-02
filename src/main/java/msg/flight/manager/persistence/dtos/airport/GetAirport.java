package msg.flight.manager.persistence.dtos.airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAirport {
    private int size;
    private int page;
    private String filter;
}
