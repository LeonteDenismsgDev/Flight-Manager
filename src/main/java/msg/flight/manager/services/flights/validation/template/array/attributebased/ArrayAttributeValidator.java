package msg.flight.manager.services.flights.validation.template.array.attributebased;

import msg.flight.manager.services.flights.validation.template.AttributeValidator;
import msg.flight.manager.services.flights.validation.template.array.typebased.ArrayRuleValidator;
import msg.flight.manager.services.flights.validation.template.extraction.AttributeExtractor;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.util.List;

public class ArrayAttributeValidator implements AttributeValidator {
    private static final AttributeExtractor attributeExtractor = new AttributeExtractor();


    @Override
    public String validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        List<BsonValue> array = attributeExtractor.extractArrayAttribute(attributeLink, flight);
        String of = validationRule.getString("of").getValue();
        BsonValue bsonValue = validationRule.get("contained");
        if (bsonValue != null) {
            if (bsonValue.isString()) {
                String contained = bsonValue.asString().getValue();
                if (of.equals("text") || of.equals("date")) {
                    if (contained.contains("\'")) {
                        bsonValue = new BsonString(contained.toLowerCase().replaceAll("\'", ""));
                    } else {
                        if (contained.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z")) {
                            bsonValue = new BsonString(contained);
                        } else {
                            bsonValue = attributeExtractor.extractBsonValue(contained, flight);
                        }
                    }
                } else {
                    bsonValue = attributeExtractor.extractBsonValue(contained, flight);
                }
            }
        }
        ArrayRuleValidator<BsonValue> validator = new ArrayRuleValidator<>();
        validator.setContained(bsonValue);
        validator.setMin(getNumberAttribute("min", flight, validationRule));
        validator.setMax(getNumberAttribute("max", flight, validationRule));
        return validator.validate(array, attributeLink);
    }

    private Integer getNumberAttribute(String ruleName, JsonObject flight, BsonDocument validationRule) {
        BsonValue ruleValue = validationRule.get(ruleName);
        if (ruleValue != null) {
            if (ruleValue.isNumber()) {
                return ruleValue.asNumber().intValue();
            }
            if (ruleValue.isString()) {
                return attributeExtractor.extractIntegerAttribute(ruleValue.asString().getValue(), flight);
            }
        }
        return null;
    }
}
