package msg.flight.manager.services.flights.validation.extraction;

import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class AttributeExtractor extends AbstractAttributeExtractor {

    public Integer extractIntegerAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = null;
        try {
            attributeValue = extractAttributeValue(attributeLink,jsonFlight);
            if (attributeValue.isNumber()) {
                return attributeValue.asNumber().intValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Float extractFloatAttribute(String attributeLink,JsonObject jsonFlight){
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink,jsonFlight);
            if(attributeValue.isNumber()){
                return (float) attributeValue.asNumber().doubleValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractTextAttribute(String attributeLink, JsonObject jsonFlight){
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink,jsonFlight);
            if(attributeValue.isString()){
                return attributeValue.asString().getValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BsonValue> extractArrayAttribute(String attributeLink, JsonObject jsonFlight){
        try{
            BsonValue attributeValue = extractAttributeValue(attributeLink,jsonFlight);
            if(attributeValue.isArray()){
                return attributeValue.asArray().getValues();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime extractLocalDateTimeAttribute(String attributeLink, JsonObject jsonFlight){
        try{
            BsonValue attributeValue = extractAttributeValue(attributeLink,jsonFlight);
            if(attributeValue.isDateTime()){
                Instant instant = Instant.ofEpochMilli(attributeValue.asDateTime().getValue());
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
