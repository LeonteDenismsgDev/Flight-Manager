package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.FloatAttributeValidator;
import org.bson.*;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;


public class FloatAttributeValidatorTest {

    private final FloatAttributeValidator floatAttributeValidator = new FloatAttributeValidator();

    @Test
    public void validate_returnsExpectedResponse_whenCalledWithInvalidJsonForRules() {
        String expected = createExpectedResponse();
        Assertions.assertEquals(expected, floatAttributeValidator.validate(createValidation(false, false, ""), createFlightJsonObject(false)));
    }

    @Test
    public void validate_throwsRuntimeException_whenCalledWithInvalidArrayRule() {
        Assertions.assertThrows(RuntimeException.class, () -> floatAttributeValidator.validate(createValidation(true, false, ""), createFlightJsonObject(false)));
    }

    @Test
    public void validate_throwsRuntimeException_whenInvalidTextArrayAttribute() {
        Assertions.assertThrows(RuntimeException.class, () -> floatAttributeValidator.validate(createValidation(true, true, "arrayAttribute"), createFlightJsonObject(true)));
    }

    @Test
    public void validate_returnsRuntimeException_whenValidTextArrayAttribute() {
        String expected = createExpectedResponse();
        Assertions.assertEquals(createExpectedResponse(), floatAttributeValidator.validate(createValidation(true, true, "arrayAttribute"), createFlightJsonObject(false)));
    }

    private String createExpectedResponse() {
        return "attribute should be less than 1.0\n" +
                "attribute should be less or equal with 1.0\n" +
                "attribute should be greater than 6.0\n" +
                "attribute should be greater or equal with 6.0\n" +
                "attribute should be a part of [23.0, 12.0, 1.0]\n";
    }

    private BsonDocument createValidation(Boolean emptyArray, Boolean stringArray, String arrayText) {
        BsonValue floatValueArray = null;
        if (!emptyArray) {
            List<BsonValue> integerArray = new ArrayList<>();
            integerArray.add(new BsonDouble(23));
            integerArray.add(new BsonDouble(12));
            integerArray.add(new BsonDouble(1));
            floatValueArray = new BsonArray(integerArray);
        } else {
            if (stringArray) {
                floatValueArray = new BsonString(arrayText);
            }else{
                floatValueArray = new BsonArray(new ArrayList<>());
            }
        }
        return new BsonDocument()
                .append("attribute", new BsonString("attribute"))
                .append("min", new BsonDouble(1))
                .append("mine", new BsonDouble(1))
                .append("max", new BsonDouble(6))
                .append("maxe", new BsonDouble(6))
                .append("equals", new BsonString("floatAttribute"))
                .append("values", floatValueArray);
    }

    private JsonObject createFlightJsonObject(Boolean emptyArray) {
        List<BsonValue> arrayContent = new ArrayList<>();
        if (!emptyArray) {
            arrayContent.add(new BsonDouble(23));
            arrayContent.add(new BsonDouble(12));
            arrayContent.add(new BsonDouble(1));
        }
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("attribute", new BsonDouble(3));
        bsonDocument.append("floatAttribute", new BsonDouble(3));
        bsonDocument.append("arrayAttribute", new BsonArray(arrayContent));
        return new JsonObject(bsonDocument.toJson());
    }
}
