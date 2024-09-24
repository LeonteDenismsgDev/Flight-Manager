package msg.flight.manager.persistence.dtos.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequest {
    private String dataType;
    private String selection;
    private Object request;
}
