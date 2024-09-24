package msg.flight.manager.persistence.dtos.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyExportRequest {
    private int minFleet;
    private int maxFleet;
    private int minCrew;
    private int maxCrew;
}
