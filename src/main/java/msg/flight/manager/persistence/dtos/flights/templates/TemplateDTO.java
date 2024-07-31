package msg.flight.manager.persistence.dtos.flights.templates;

import lombok.Getter;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

@Getter
public class TemplateDTO {
    @Id
    private String name;
    private Set<TemplateAttribute> attributes;
    private List<ValidationDTO> validations;
}