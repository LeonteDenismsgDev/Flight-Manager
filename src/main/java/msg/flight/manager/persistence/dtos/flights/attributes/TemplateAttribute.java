package msg.flight.manager.persistence.dtos.flights.attributes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.*;

import java.util.HashMap;
import java.util.Map;

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
            this.defaultValue = convertBsonValue(document.get("defaultValue"));
        }
        this.description = document.getString("description").getValue();
    }

    private Object convertBsonValue(BsonValue bsonValue) {
        if (bsonValue instanceof BsonString) {
            return bsonValue.asString().getValue();
        }else if (bsonValue instanceof BsonInt32) {
            return bsonValue.asInt32().getValue();
        }else if (bsonValue instanceof BsonDouble) {
            return bsonValue.asDouble().getValue();
        } else if (bsonValue instanceof BsonDocument) {
            return getObjectMap(bsonValue.asDocument());
        } else {
            return null;
        }
    }

    private Map<String,Object> getObjectMap(BsonDocument document){
        Map<String,Object> objectMap = new HashMap<>();
        for(String key : document.keySet()){
            objectMap.put(key,convertBsonValue(document.get(key)));
        }
        return  objectMap;
    }
}
