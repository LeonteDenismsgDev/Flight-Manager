package msg.flight.manager.services.flights.validation.extraction;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.util.regex.Pattern;

public abstract class AbstractAttributeExtractor {

    public BsonValue extractAttributeValue(String attributeLink, JsonObject jsonFlight) throws ClassNotFoundException {
        BsonDocument bsonFlight = jsonFlight.toBsonDocument();
        if (!attributeLink.contains(".")) {
            return bsonFlight.get(attributeLink);
        }
        BsonValue deepAttributeValue = bsonFlight;
        for (String attribute : deepListAttribute(attributeLink)) {
            if (!deepAttributeValue.isNull() && deepAttributeValue.isDocument()) {
                deepAttributeValue = deepAttributeValue.asDocument().get(attribute);
                if (deepAttributeValue.isNull()) {
                    throw new RuntimeException("Invalid rule validation link");
                }
            }else{
                throw new RuntimeException("Invalid attribute link");
            }
        }
        return deepAttributeValue;
    }

    private String[] deepListAttribute(String deepAttributeDescription) {
        return deepAttributeDescription.split(Pattern.quote("."));
    }
}
