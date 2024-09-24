package msg.flight.manager.services.flights.validation.template.text.attributebased;

import lombok.SneakyThrows;
import msg.flight.manager.services.flights.validation.template.AttributeValidator;
import msg.flight.manager.services.flights.validation.template.extraction.AttributeExtractor;
import msg.flight.manager.services.flights.validation.template.text.typebased.StringRuleValidator;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.lang.reflect.Field;
import java.util.List;

public class StringAttributeValidator implements AttributeValidator {
    private static final AttributeExtractor attributeExtractor = new AttributeExtractor();

    @Override
    @SneakyThrows
    public String validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        String attributeTextValue = attributeExtractor.extractTextAttribute(attributeLink, flight);
        StringRuleValidator ruleValidator = extractTextValidator(validationRule, flight);
        return ruleValidator.validate(attributeTextValue, attributeLink);
    }

    private StringRuleValidator extractTextValidator(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        StringRuleValidator textValidationRules = new StringRuleValidator();
        for (Field field : textValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if (validationRule != null) {
                if (validationRule.isString()) {
                    String text = validationRule.asString().getValue();
                    if (text.contains("'")) {
                        field.set(textValidationRules, text.replaceAll("'", ""));
                    } else {
                        if (field.getName().equals("values")) {
                            List<BsonValue> bsonValues = attributeExtractor.extractArrayAttribute(text, flight);
                            List<String> stringValues = bsonValues.stream().filter(BsonValue::isString).map(bv -> bv.asString().getValue()).toList();
                            if (!stringValues.isEmpty()) {
                                field.set(textValidationRules, stringValues);
                            } else {
                                throw new RuntimeException("Invalid array type");
                            }
                        } else {
                            String ruleFloatAttributeValue = attributeExtractor.extractTextAttribute(text, flight);
                            field.set(textValidationRules, ruleFloatAttributeValue);
                        }
                    }
                } else {
                    if (validationRule.isArray()) {
                        List<String> bsonValues = validationRule.asArray().getValues().stream().filter(BsonValue::isString).map(bv -> bv.asString().getValue()).toList();
                        if (!bsonValues.isEmpty()) {
                            field.set(textValidationRules, bsonValues);
                        } else {
                            throw new RuntimeException("Invalid array type");
                        }
                    }else{
                        throw  new RuntimeException("Invalid rule sequence");
                    }
                }
            }
        }
        return textValidationRules;
    }
}
