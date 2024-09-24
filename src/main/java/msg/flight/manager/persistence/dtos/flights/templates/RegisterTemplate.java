package msg.flight.manager.persistence.dtos.flights.templates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import msg.flight.manager.persistence.annotations.ContainsValidRules;
import msg.flight.manager.persistence.annotations.DoesNotHaveMandatoryAttributes;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.dtos.flights.serialization.JsonObjectListDeserializer;
import msg.flight.manager.persistence.dtos.flights.serialization.JsonObjectListSerializer;
import org.bson.json.JsonObject;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class RegisterTemplate {
    @NotBlank(message = "The template name should not be empty")
    @NotNull(message = "The template name should not be null")
    private String name;
    @DoesNotHaveMandatoryAttributes(message = "Invalid attribute list")
    private Set<TemplateAttribute> attributes;
    @ContainsValidRules(message = "Invalid rule structure")
    @JsonDeserialize(using = JsonObjectListDeserializer.class)
    @JsonSerialize(using = JsonObjectListSerializer.class)
    private List<JsonObject> validations;
}
