package msg.flight.manager.persistence.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.util.List;

public class AttributeRulesValidator implements ConstraintValidator<ContainsValidRules, List<JsonObject>> {

    private static final List<String> validTypes = List.of("text", "number", "precision_number", "date", "array", "user", "company" ,"object");

    @Override
    public boolean isValid(List<JsonObject> templateAttributesRules, ConstraintValidatorContext constraintValidatorContext) {
        if (templateAttributesRules == null || templateAttributesRules.isEmpty()) {
            return false;
        }
        for (JsonObject rule : templateAttributesRules) {
            BsonDocument bsonRule = rule.toBsonDocument();
            if (!bsonRule.containsKey("attribute") || !bsonRule.containsKey("type")) {
                return false;
            }
            BsonValue ruleAttribute = bsonRule.get("attribute");
            if (ruleAttribute.isString()) {
                if (!ruleAttribute.asString().getValue().matches("^[a-zA-Z]+(\\.[a-zA-Z]+)?$")) {
                    return false;
                }
            }else{
                return false;
            }
            BsonValue typeAttribute = bsonRule.get("type");
            if (typeAttribute.isString()) {
                if(!validTypes.contains(typeAttribute.asString().getValue())) {
                    return false;
                }else{
                    String type = typeAttribute.asString().getValue();
                    switch (type) {
                        case "text":
                            if (!(bsonRule.containsKey("containedIn") || bsonRule.containsKey("equalWith") || bsonRule.containsKey("values"))) {
                                return false;
                            }
                            break;
                        case "date":
                            if (!(bsonRule.containsKey("before") || bsonRule.containsKey("after") || (bsonRule.containsKey("start") && bsonRule.containsKey("end")))) {
                                return false;
                            }
                            break;
                        case "array":
                            if (bsonRule.containsKey("of") && !(bsonRule.containsKey("max") || bsonRule.containsKey("min"))) {
                                return false;
                            }
                        default:
                            if (!(bsonRule.containsKey("min") || bsonRule.containsKey("mine") || bsonRule.containsKey("max") || bsonRule.containsKey("maxe") || bsonRule.containsKey("equals") || bsonRule.containsKey("values"))) {
                                return false;
                            }
                            break;
                    }
                }
            }else{
                return false;
            }
        }
        return true;
    }

}
