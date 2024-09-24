package msg.flight.manager.services.flights.validation.template.extraction;

import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public class AttributeExtractor extends AbstractAttributeExtractor {

    public Integer extractIntegerAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = null;
        attributeValue = extractAttributeValue(attributeLink, jsonFlight);
        if (attributeValue.isNumber()) {
            return attributeValue.asNumber().intValue();
        }
        throw new RuntimeException("Invalid attribute");
    }

    public Float extractFloatAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
        if (attributeValue.isNumber()) {
            return (float) attributeValue.asNumber().doubleValue();
        }
        throw new RuntimeException("Invalid attribute");
    }

    public String extractTextAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
        if (attributeValue.isString()) {
            return attributeValue.asString().getValue();
        }
        throw new RuntimeException("Invalid attribute");
    }

    public List<BsonValue> extractArrayAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
        if (attributeValue.isArray()) {
            return attributeValue.asArray().getValues();
        }
        throw new RuntimeException("Invalid attribute");
    }

    public BsonValue extractBsonValue(String attributeLink, JsonObject jsonFlight) {
        return extractAttributeValue(attributeLink, jsonFlight);
    }

    public LocalDateTime extractLocalDateTimeAttribute(String attributeLink, JsonObject jsonFlight) {
        BsonValue attributeValue = extractAttributeValue(attributeLink, jsonFlight);
        if (attributeValue.isString()) {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(attributeValue.asString().getValue());
            return offsetDateTime.toLocalDateTime();
        }
        throw new RuntimeException("Invalid attribute");
    }

}
