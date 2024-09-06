package msg.flight.manager.persistence.dtos.plane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.airport.AirportDataTableView;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaneTableResult {

    private int max_planes;
    private List<PlaneDataTableView> page;
}
