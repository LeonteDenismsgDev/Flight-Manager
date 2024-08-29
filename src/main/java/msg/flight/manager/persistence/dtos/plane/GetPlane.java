package msg.flight.manager.persistence.dtos.plane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPlane {

    private int size;
    private int page;
    private String filter;
}
