package msg.flight.manager.services.flights.validation.extraction;

import msg.flight.manager.persistence.repositories.ValidationRepository;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractAttributeExtractor {
    @Autowired
    private ValidationRepository repository;

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
                    String collection = deepAttributeValue.asDocument().getString("collection").getValue();
                    String id = deepAttributeValue.asDocument().getString("id").getValue();
                    if(collection == null || id == null){
                        throw new RuntimeException("Invalid attribute link");
                    }
                    BsonDocument dbDocument = repository.getDocument(collection, id);
                    deepAttributeValue = dbDocument.get(attribute);
                }
            }else{
                throw new RuntimeException("Invalid attribute link");
            }
        }
        return deepAttributeValue;
    }

    private String[] deepListAttribute(String deepAttributeDescription) {
        return deepAttributeDescription.split(".+");
    }
}
