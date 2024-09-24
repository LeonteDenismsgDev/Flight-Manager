package msg.flight.manager.services.flights.validation.template;

import org.bson.BsonDocument;
import org.bson.json.JsonObject;

public interface AttributeValidator {
    String validate(BsonDocument validationRule, JsonObject flight);
}
