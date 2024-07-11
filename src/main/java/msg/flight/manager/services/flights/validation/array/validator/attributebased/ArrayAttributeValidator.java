package msg.flight.manager.services.flights.validation.array.validator.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.array.validator.typebased.ArrayRuleValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArrayAttributeValidator implements AttributeValidator {
    @Autowired
    private AttributeExtractor attributeExtractor;


    @Override
    public boolean validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        List<BsonValue> array = attributeExtractor.extractArrayAttribute(attributeLink,flight);
        String of = validationRule.getString("of").getValue();
        Integer min = validationRule.getNumber("min").asNumber().intValue();
        Integer max = validationRule.getNumber("max").asNumber().intValue();
        BsonValue contained = validationRule.get("contained");
        if(of.contains("String")){
            return validateStringArray(array,contained,min,max);
        }
        if(of.contains("Integer")){
            return  validateIntegerArray(array,contained,min,max);
        }
        if(of.contains("Float")){
            return validateFloatArray(array,contained,min,max);
        }
        return true;
    }

    private boolean validateFloatArray(List<BsonValue> array, BsonValue contained, Integer min, Integer max) {
        List<Float> stringArray = array.stream().filter(BsonValue::isNumber).map(bv -> (float)bv.asNumber().doubleValue()).toList();
        if(!contained.isNumber()){
            throw new RuntimeException();
        }
        ArrayRuleValidator<Float> arrayRuleValidator = new ArrayRuleValidator<>((float)contained.asNumber().doubleValue(),max,min);
        return arrayRuleValidator.validate(stringArray);
    }

    private boolean validateIntegerArray(List<BsonValue> array, BsonValue contained, Integer min, Integer max) {
        List<Integer> stringArray = array.stream().filter(BsonValue::isNumber).map(bv -> bv.asNumber().intValue()).toList();
        if(!contained.isNumber()){
            throw new RuntimeException();
        }
        ArrayRuleValidator<Integer> arrayRuleValidator = new ArrayRuleValidator<>(contained.asNumber().intValue(),max,min);
        return arrayRuleValidator.validate(stringArray);
    }

    private boolean validateStringArray(List<BsonValue> array, BsonValue contained, Integer min, Integer max){
        List<String> stringArray = array.stream().filter(BsonValue::isString).map(bv -> bv.asString().getValue()).toList();
        if(!contained.isString()){
            throw new RuntimeException();
        }
        ArrayRuleValidator<String> arrayRuleValidator = new ArrayRuleValidator<>(contained.asString().getValue(),max,min);
        return arrayRuleValidator.validate(stringArray);
    }
}
