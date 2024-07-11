package msg.flight.manager.persistence.models.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import org.bson.json.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "templates")
public class DBTemplate {
    @Id
    private String name;
    private String createdBy;
    private Set<TemplateAttribute> attributes;
    private List<JsonObject> validations;
}
