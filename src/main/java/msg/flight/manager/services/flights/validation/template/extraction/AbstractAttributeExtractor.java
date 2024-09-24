package msg.flight.manager.services.flights.validation.template.extraction;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.util.regex.Pattern;

public abstract class AbstractAttributeExtractor {

    public BsonValue extractAttributeValue(String attributeLink, JsonObject jsonFlight) {
        BsonDocument bsonFlight = jsonFlight.toBsonDocument();
        if (!attributeLink.contains(".")) {
            if(!bsonFlight.containsKey(attributeLink)){
                throw new RuntimeException("Invalid attribute rule");
            }
            return bsonFlight.get(attributeLink);
        }
        BsonValue deepAttributeValue = bsonFlight;
        for (String attribute : deepListAttribute(attributeLink)) {
            if (deepAttributeValue != null && deepAttributeValue.isDocument()) {
                deepAttributeValue = deepAttributeValue.asDocument().get(attribute);
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
