package msg.flight.manager.services.flights.validation.array.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.array.typebased.ArrayRuleValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.util.List;

public class ArrayAttributeValidator implements AttributeValidator {
    private static final AttributeExtractor attributeExtractor = new AttributeExtractor();


    @Override
    public String validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        List<BsonValue> array = attributeExtractor.extractArrayAttribute(attributeLink,flight);
        String of = validationRule.getString("of").getValue();
         BsonValue bsonValue = validationRule.get("contained");
         if(bsonValue != null){
             if(bsonValue.isString()){
                 String contained = bsonValue.asString().getValue();
                 if(of.equals("text") || of.equals("date")){
                     String attr;
                     int indexOfPeriod = contained.indexOf('.');
                     if(indexOfPeriod == -1){
                         attr = contained;
                     }else {
                         attr = contained.substring(0, indexOfPeriod);
                     }
                     if(flight.toBsonDocument().containsKey(attr)){
                         bsonValue = attributeExtractor.extractBsonValue(attr, flight);
                     }
                 }else{
                     bsonValue = attributeExtractor.extractBsonValue(contained, flight);
                 }
             }
         }
        ArrayRuleValidator<BsonValue> validator = new ArrayRuleValidator<>();
        validator.setContained(bsonValue);
        validator.setMin(getNumberAttribute("min",flight,validationRule));
        validator.setMax(getNumberAttribute("max",flight,validationRule));
        return validator.validate(array,attributeLink);
    }

    private Integer getNumberAttribute(String ruleName, JsonObject flight, BsonDocument validationRule){
        BsonValue ruleValue = validationRule.get(ruleName);
        if(ruleValue != null){
            if(ruleValue.isNumber()){
                return ruleValue.asNumber().intValue();
            }
            if(ruleValue.isString()){
                return attributeExtractor.extractIntegerAttribute(ruleValue.asString().getValue(),flight);
            }
        }
         return null;
    }
}
