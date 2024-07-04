package msg.flight.manager.services.flights.validation.number.validators.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import msg.flight.manager.services.flights.validation.number.validators.typebased.NumberRulesValidator;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class FloatAttributeValidator implements AttributeValidator {
    @Autowired
    private AttributeExtractor attributeExtractor;

    @Override
    public boolean validate(BsonDocument validationRule, JsonObject flight) {
            String attributeLink = validationRule.getString("attribute").getValue();
            Float attributeFloatValue = attributeExtractor.extractFloatAttribute(attributeLink,flight);
        try {
            NumberRulesValidator<Float> ruleValidator = extractFloatRules(validationRule,flight);
            return ruleValidator.validate(attributeFloatValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private NumberRulesValidator<Float> extractFloatRules(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        NumberRulesValidator<Float> numberValidationRules = new NumberRulesValidator<>();
        for (Field field : numberValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if(validationRule.isNumber()){
                field.set(numberValidationRules,(float)validationRule.asNumber().doubleValue());
            }
            if(validationRule.isArray()){
                List<Float> bsonValues =  validationRule.asArray().getValues().stream().filter(BsonValue::isNumber).map(bv-> (float)bv.asNumber().doubleValue()).toList();
                field.set(numberValidationRules,bsonValues);
            }
            if(validationRule.isString()){
                String  ruleAttributeLink = validationRule.asString().getValue();
                if(field.getName().equals("values")){
                    List<BsonValue> bsonValues =  attributeExtractor.extractArrayAttribute(ruleAttributeLink,flight);
                    List<Float> integerValues = bsonValues.stream().filter(BsonValue::isNumber).map(bv-> (float)bv.asNumber().doubleValue()).toList();
                    field.set(numberValidationRules,integerValues);
                }else {
                    Float ruleFloatAttributeValue = attributeExtractor.extractFloatAttribute(ruleAttributeLink, flight);
                    field.set(numberValidationRules, ruleFloatAttributeValue);
                }
            }
        }
        return numberValidationRules;
    }
}
