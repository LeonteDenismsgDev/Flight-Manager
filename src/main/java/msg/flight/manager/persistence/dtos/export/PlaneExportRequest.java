package msg.flight.manager.persistence.dtos.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaneExportRequest {
    private String manufacturer;
    private String model;
    private String company;
    private int minYear;
    private int maxYear;
}
