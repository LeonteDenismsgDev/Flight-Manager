package msg.flight.manager.persistence.dtos.airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportTableResult {
    private int max_airports;
    private List<AirportDataTableView> page;
}
