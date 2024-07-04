package msg.flight.manager.persistence.dtos.flights.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.json.JsonObject;

import java.io.IOException;
import java.util.List;

public class JsonObjectListSerializer extends JsonSerializer<List<JsonObject>> {
    @Override
    public void serialize(List<JsonObject> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        gen.writeStartArray();
        for (JsonObject jsonObject : value) {
            gen.writeTree(objectMapper.readTree(extractObjectValue(jsonObject)));
        }
        gen.writeEndArray();
    }

    private String extractObjectValue(JsonObject jsonObject) {
        String content = jsonObject.toString().replace("\\", "");
        return content.substring(10, content.length() - 2);
    }


}
