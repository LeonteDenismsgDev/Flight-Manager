package msg.flight.manager.persistence.dtos.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExportRequest {
    public String company;
    public List<String> role;
}
