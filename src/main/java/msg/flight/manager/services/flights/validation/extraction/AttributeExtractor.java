package msg.flight.manager.services.flights.validation.extraction;

import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public class AttributeExtractor extends AbstractAttributeExtractor {

    public Integer extractIntegerAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = null;
        try {
            attributeValue = extractAttributeValue(attributeLink, jsonFlight);
            if (attributeValue.isNumber()) {
                return attributeValue.asNumber().intValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Float extractFloatAttribute(String attributeLink, JsonObject jsonFlight) {
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
            if (attributeValue.isNumber()) {
                return (float) attributeValue.asNumber().doubleValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractTextAttribute(String attributeLink, JsonObject jsonFlight) {
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
            if (attributeValue.isString()) {
                return attributeValue.asString().getValue();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BsonValue> extractArrayAttribute(String attributeLink, JsonObject jsonFlight) {
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
            if (attributeValue.isArray()) {
                return attributeValue.asArray().getValues();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BsonValue extractBsonValue(String attributeLink, JsonObject jsonFlight){
        try {
            return extractAttributeValue(attributeLink,jsonFlight);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime extractLocalDateTimeAttribute(String attributeLink, JsonObject jsonFlight) {
        try {
            BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
            if (attributeValue.isString()) {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(attributeValue.asString().getValue());
                return offsetDateTime.toLocalDateTime();
            }
            throw new RuntimeException("Invalid attribute");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
