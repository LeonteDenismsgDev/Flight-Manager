package msg.flight.manager.persistence.dtos.flights.templates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MapTemplateDTO {
    private String name;
    private Set<TemplateAttribute> attributes;
    private List<Map<String,Object>> validations;
}
