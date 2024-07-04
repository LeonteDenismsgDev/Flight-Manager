package msg.flight.manager.persistence.dtos.flights.attributes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.BsonDocument;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class TemplateAttribute {
    @NotNull(message = "the name should not be null")
    @NotBlank(message = "the name should not be empty")
    private String name;
    @NotNull(message = "the label should not be null")
    @NotBlank(message = "the label should not be empty")
    private String label;
    private boolean required;
    @NotNull(message = "the type should not be null")
    @NotBlank(message = "the type should not be empty")
    private String type;
    private Object defaultValue;
    @NotNull(message = "the description should not be null")
    @NotBlank(message = "the description should not be empty")
    private String description;

    public TemplateAttribute(BsonDocument document) {
        this.name = document.getString("name").getValue();
        this.label = document.getString("label").getValue();
        this.required = document.getBoolean("required").getValue();
        this.type = document.getString("type").getValue();
        if (document.containsKey("defaultValue")) {
            this.defaultValue = document.getObjectId("defaultValue").getValue();
        }
        this.description = document.getString("description").getValue();
    }
}
