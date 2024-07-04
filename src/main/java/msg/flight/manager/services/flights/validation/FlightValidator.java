package msg.flight.manager.services.flights.validation;

import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import msg.flight.manager.services.flights.validation.number.validators.typebased.NumberRulesValidator;
import msg.flight.manager.services.flights.validation.number.validators.typebased.NumberValidator;
import msg.flight.manager.services.flights.validation.text.validators.typebased.StringValidator;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class FlightValidator {
    private static final NumberValidator<Integer> integerValidator = new NumberValidator<>();
    private static final NumberValidator<Float> floatValidator = new NumberValidator<>();
    private static final StringValidator stringValidator = new StringValidator();
    @Autowired
    private AttributeExtractor attributeExtractor;

    public void validate(JsonObject flight, DBTemplate template) throws NoSuchFieldException, IllegalAccessException {
        for (JsonObject validationRule : template.getValidations()) {
            /*BsonDocument bsonDocument = validationRule.toBsonDocument();
            String type = bsonDocument.get("type").asString().getValue();
            String attribute = bsonDocument.get("attribute").asString().getValue();
            String[] deepAttributes = deepListAttribute(attribute);
            Object attr = getFieldValue(flight,deepAttributes[0]);
            for (int index = 1; index < deepAttributes.length;index++) {
                if(attr instanceof JsonObject){
                    attr = ((JsonObject) attr).toBsonDocument().get(deepAttributes[index]).asObjectId().getValue();
                }
                attr = getFieldValue(attr, deepAttributes[index]);
            }
            if (!getTypeClass(type).isInstance(attr)) {
                throw new RuntimeException("");
            }*/
        }
    }

    private NumberRulesValidator getValidationRules(BsonDocument validationRule) throws IllegalAccessException {
        NumberRulesValidator validationRules = new NumberRulesValidator();
        Field[] rules = NumberRulesValidator.class.getDeclaredFields();
        for (Field rule : rules) {
            BsonValue ruleValue = validationRule.get(rule.getName());
            rule.set(validationRules,ruleValue.toString());
        }
        return validationRules;
    }


    private Object getFieldValue(Object object, String filed) throws NoSuchFieldException, IllegalAccessException {
        Field attribute = object.getClass().getDeclaredField(filed);
        attribute.setAccessible(true);
        return attribute.get(object);
    }

    private Class<?> getTypeClass(String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
