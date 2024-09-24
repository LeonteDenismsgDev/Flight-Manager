package msg.flight.manager.persistence.dtos.flights;

import lombok.*;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class TemplateTableResult {
    private Integer templatesCount;
    private List<RegisterTemplate> page;
}
