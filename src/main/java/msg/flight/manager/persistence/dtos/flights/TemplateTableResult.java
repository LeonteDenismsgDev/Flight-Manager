package msg.flight.manager.persistence.dtos.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TemplateTableResult {
    private Integer templatesCount;
    private List<RegisterTemplate> page;
}
