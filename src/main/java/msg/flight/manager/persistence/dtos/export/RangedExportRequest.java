package msg.flight.manager.persistence.dtos.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RangedExportRequest {
    private int minRange;
    private int maxRange;
}
