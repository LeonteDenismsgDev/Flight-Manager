package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.IntegerAttributeValidator;
import org.bson.*;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public class IntegerAttributeValidatorTest {

    private final IntegerAttributeValidator integerAttributeValidator = new IntegerAttributeValidator();

    @Test
    public void validate_returnsExpectedResponse_whenCalledWithInvalidJsonForRules() {
        String expected = createExpectedResponse();
        Assertions.assertEquals(expected, integerAttributeValidator.validate(createValidation(false, false, ""), createFlightJsonObject(false)));
    }

    @Test
    public void validate_throwsRuntimeException_whenCalledWithInvalidArrayRule() {
        Assertions.assertThrows(RuntimeException.class, () -> integerAttributeValidator.validate(createValidation(true, false, ""), createFlightJsonObject(false)));
    }

    @Test
    public void validate_throwsRuntimeException_whenInvalidTextArrayAttribute() {
        Assertions.assertThrows(RuntimeException.class, () -> integerAttributeValidator.validate(createValidation(true, true, "arrayAttribute"), createFlightJsonObject(true)));
    }

    @Test
    public void validate_returnsRuntimeException_whenValidTextArrayAttribute() {
        String expected = createExpectedResponse();
        Assertions.assertEquals(createExpectedResponse(), integerAttributeValidator.validate(createValidation(true, true, "arrayAttribute"), createFlightJsonObject(false)));
    }

    private String createExpectedResponse() {
        return "attribute should be less than 1\n" +
                "attribute should be less or equal with 1\n" +
                "attribute should be greater than 6\n" +
                "attribute should be greater or equal with 6\n" +
                "attribute should be a part of [23, 12, 1]\n";
    }

    private BsonDocument createValidation(Boolean emptyArray, Boolean stringArray, String arrayText) {
        BsonValue integerValueArray = null;
        if (!emptyArray) {
            List<BsonValue> integerArray = new ArrayList<>();
            integerArray.add(new BsonInt32(23));
            integerArray.add(new BsonInt32(12));
            integerArray.add(new BsonInt32(1));
            integerValueArray = new BsonArray(integerArray);
        } else {
            if (stringArray) {
                integerValueArray = new BsonString(arrayText);
            }else{
                integerValueArray = new BsonArray(new ArrayList<>());
            }
        }
        return new BsonDocument()
                .append("attribute", new BsonString("attribute"))
                .append("min", new BsonInt32(1))
                .append("mine", new BsonInt32(1))
                .append("max", new BsonInt32(6))
                .append("maxe", new BsonInt32(6))
                .append("equals", new BsonString("integerAttribute"))
                .append("values", integerValueArray);
    }

    private JsonObject createFlightJsonObject(Boolean emptyArray) {
        List<BsonValue> arrayContent = new ArrayList<>();
        if (!emptyArray) {
            arrayContent.add(new BsonInt32(23));
            arrayContent.add(new BsonInt32(12));
            arrayContent.add(new BsonInt32(1));
        }
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("attribute", new BsonInt32(3));
        bsonDocument.append("integerAttribute", new BsonInt32(3));
        bsonDocument.append("arrayAttribute", new BsonArray(arrayContent));
        return new JsonObject(bsonDocument.toJson());
    }
}
