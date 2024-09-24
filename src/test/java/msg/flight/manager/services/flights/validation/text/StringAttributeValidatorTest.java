package msg.flight.manager.services.flights.validation.text;

import msg.flight.manager.services.flights.validation.template.text.attributebased.StringAttributeValidator;
import org.bson.*;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class StringAttributeValidatorTest {
    private StringAttributeValidator stringValidator = new StringAttributeValidator();

    @Test
    public void validate_returnsExpectedResponse_whenAllValidationRulesAreSet() {
        String expected = "";
        Assertions.assertEquals(expected, stringValidator.validate(createValidationDocument(false,false,false), createJsonFlight(true)));
    }

    @Test
    public void validate_throwsRuntimeException_whenInvalidTextArray() {
        Assertions.assertThrows(RuntimeException.class, () -> stringValidator.validate(createValidationDocument(true,false,false), createJsonFlight(true)));
    }

    @Test
    public void validate_returnsExpectedResponse_whenArrayRuleUsesStringAttributeReference() {
        String expected = "";
        Assertions.assertEquals(expected, stringValidator.validate(createValidationDocument(false,true,false),createJsonFlight(false)));
    }

    @Test
    public void validate_throwsRuntimeException_whenEmptyArrayAttributeReference(){
        Assertions.assertThrows(RuntimeException.class,()->stringValidator.validate(createValidationDocument(true,true,false),createJsonFlight(true)));
    }

    @Test
    public void validate_throwsRuntimeException_whenDifferentTypeAttribute(){
        Assertions.assertThrows(RuntimeException.class,()->stringValidator.validate(createValidationDocument(true,true,true),createJsonFlight(false)));
    }

    private JsonObject createJsonFlight(Boolean emptyArray) {
        List<BsonValue> textArray = new ArrayList<>();
        if (!emptyArray) {
            textArray.add(new BsonString("firstValue"));
            textArray.add(new BsonString("secondValue"));
            textArray.add(new BsonString("equalWith"));
        }
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("attribute", new BsonString("equalWith"));
        bsonDocument.append("textAttribute", new BsonString("equalWith"));
        bsonDocument.append("arrayAttribute", new BsonArray(textArray));
        return new JsonObject(bsonDocument.toJson());
    }

    private BsonDocument createValidationDocument(Boolean emptyArray, Boolean arrayTextAttribute, Boolean differentTypeAttribute) {
        List<BsonValue> ruleArrayValidator = new ArrayList<>();
        if (!emptyArray) {
            ruleArrayValidator.add(new BsonString("firstValue"));
            ruleArrayValidator.add(new BsonString("secondValue"));
            ruleArrayValidator.add(new BsonString("equalWith"));
        }
        BsonValue arrayRule = new BsonArray(ruleArrayValidator);
        if (arrayTextAttribute) {
            arrayRule = new BsonString("arrayAttribute");
        }
        BsonValue equalsWith = new BsonString("\'equalWith\'");
        if(differentTypeAttribute){
            equalsWith = new BsonInt32(32);
        }
        return new BsonDocument()
                .append("attribute", new BsonString("attribute"))
                .append("containedIn", new BsonString("textAttribute"))
                .append("equalWith", equalsWith)
                .append("values", arrayRule);
    }
}
