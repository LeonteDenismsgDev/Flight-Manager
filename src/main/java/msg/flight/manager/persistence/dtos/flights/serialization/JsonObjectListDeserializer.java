package msg.flight.manager.persistence.dtos.flights.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonObjectListDeserializer extends JsonDeserializer<List<JsonObject>> {
    @Override
    public List<JsonObject> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        List<JsonObject> jsonObjects = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.isArray()) {
            for (JsonNode jsonNode : node) {
                jsonObjects.add(new JsonObject(jsonNode.toString()));  // Use the constructor of JsonObject
            }
        } else {
            throw new JsonProcessingException("Expected an array") {
            };
        }
        return jsonObjects;
    }
}
