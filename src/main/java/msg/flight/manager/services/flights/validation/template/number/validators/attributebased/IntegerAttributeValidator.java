package msg.flight.manager.services.flights.validation.template.number.validators.attributebased;

import lombok.SneakyThrows;
import msg.flight.manager.services.flights.validation.template.AttributeValidator;
import msg.flight.manager.services.flights.validation.template.extraction.AttributeExtractor;
import msg.flight.manager.services.flights.validation.template.number.validators.typebased.NumberRulesValidator;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.lang.reflect.Field;
import java.util.List;

public class IntegerAttributeValidator implements AttributeValidator {
    private static final AttributeExtractor attributeExtractor = new AttributeExtractor();

    @Override
    @SneakyThrows
    public String validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        Integer attributeFloatValue = attributeExtractor.extractIntegerAttribute(attributeLink, flight);
        NumberRulesValidator<Integer> ruleValidator = extractIntegerRules(validationRule, flight);
        return ruleValidator.validate(attributeFloatValue, attributeLink);
    }

    private NumberRulesValidator<Integer> extractIntegerRules(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        NumberRulesValidator<Integer> numberValidationRules = new NumberRulesValidator<>();
        for (Field field : numberValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if (validationRule != null) {
                if (validationRule.isNumber()) {
                    field.set(numberValidationRules, validationRule.asNumber().intValue());
                }
                if (validationRule.isArray()) {
                    List<Integer> bsonValues = validationRule.asArray().getValues().stream().filter(BsonValue::isNumber).map(bv -> bv.asNumber().intValue()).toList();
                    if (!bsonValues.isEmpty()) {
                        field.set(numberValidationRules, bsonValues);
                    } else {
                        throw new RuntimeException("Invalid array type");
                    }
                }
                if (validationRule.isString()) {
                    String ruleAttributeLink = validationRule.asString().getValue();
                    if (field.getName().equals("values")) {
                        List<BsonValue> bsonValues = attributeExtractor.extractArrayAttribute(ruleAttributeLink, flight);
                        List<Integer> integerValues = bsonValues.stream().filter(BsonValue::isNumber).map(bv -> bv.asNumber().intValue()).toList();
                        if (!integerValues.isEmpty()) {
                            field.set(numberValidationRules, integerValues);
                        } else {
                            throw new RuntimeException("Invalid array type");
                        }
                    } else {
                        Integer ruleFloatAttributeValue = attributeExtractor.extractIntegerAttribute(ruleAttributeLink, flight);
                        field.set(numberValidationRules, ruleFloatAttributeValue);
                    }
                }
            }
        }
        return numberValidationRules;
    }
}
