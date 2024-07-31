package msg.flight.manager.services.flights.validation.text.validators.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import msg.flight.manager.services.flights.validation.text.validators.typebased.StringRuleValidator;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class StringAttributeValidator implements AttributeValidator {
    @Autowired
    private AttributeExtractor attributeExtractor;

    @Override
    public boolean validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        String attributeTextValue = attributeExtractor.extractTextAttribute(attributeLink, flight);
        try {
            StringRuleValidator ruleValidator = extractTextValidator(validationRule, flight);
            return ruleValidator.validate(attributeTextValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private StringRuleValidator extractTextValidator(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        StringRuleValidator textValidationRules = new StringRuleValidator();
        for (Field field : textValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if (validationRule.isString()) {
                String text = validationRule.asString().getValue();
                if (text.contains("'")) {
                    field.set(textValidationRules, text.replaceAll("'", ""));
                } else {
                    if (field.getName().equals("values")) {
                        List<BsonValue> bsonValues = attributeExtractor.extractArrayAttribute(text, flight);
                        List<String> stringValues = bsonValues.stream().filter(BsonValue::isString).map(bv -> bv.asString().getValue()).toList();
                        field.set(textValidationRules, stringValues);
                    } else {
                        String ruleFloatAttributeValue = attributeExtractor.extractTextAttribute(text, flight);
                        field.set(textValidationRules, ruleFloatAttributeValue);
                    }
                }
            }
            if (validationRule.isArray()) {
                List<String> bsonValues = validationRule.asArray().getValues().stream().filter(BsonValue::isString).map(bv -> bv.asString().getValue()).toList();
                field.set(textValidationRules, bsonValues);
            }
        }
        return textValidationRules;
    }
}
