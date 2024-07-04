package msg.flight.manager.services.flights.validation;

import org.bson.BsonDocument;
import org.bson.json.JsonObject;

public interface AttributeValidator {
    public boolean validate(BsonDocument validationRule, JsonObject flight);
}
