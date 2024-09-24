package msg.flight.manager.services.flights.validation.array;

import msg.flight.manager.services.flights.validation.template.array.attributebased.ArrayAttributeValidator;
import org.bson.*;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class ArrayAttributeValidatorTest {

    private final ArrayAttributeValidator attributeValidator = new ArrayAttributeValidator();

    @Test
    public void validate_returnsExpectedResult_whenCalledWithAllRules() {
        String expected = "attr length should be less than 3\n";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(true, true, true, "\'text\'", "text", "text"), createFlightJsonObject("text")));
    }

    @Test
    public void validate_returnsExpectedResult_whenWithoutRules() {
        String expected = "";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(false, false, false, "\'text\'", "text", "text"), createFlightJsonObject("text")));
    }

    @Test
    public void validate_returnsExpectedResult_whenNumberArrayRulesValidation() {
        String expected = "";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(true, false, false, 23, "number", "number"), createFlightJsonObject("number")));
    }

    @Test
    public void validate_returnsExpectedResult_whenDateArrayRulesValidation() {
        String expected = "";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(true, false, false, "2024-08-27T11:52:53.145Z", "date", "date"), createFlightJsonObject("date")));
    }

    @Test
    public void validate_returnsExpectedResult_whenNumberArrayTextRulesValidation() {
        String expected = "attr should contain " + 3 + "\n";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(true, false, false, "intAttribute", "attributeName", "number"), createFlightJsonObject("number")));
    }

    @Test
    public void validate_returnsExpectedResult_whenDateArrayTextRulesValidation() {
        String expected = "";
        Assertions.assertEquals(expected,attributeValidator.validate(createValidationRules(true,false,false,"dateAttribute","date","date"),createFlightJsonObject("date")));
    }

    private BsonDocument createValidationRules(Boolean hasContained, Boolean hasMin, Boolean hasMax, Object value, String valueCase, String of) {
        BsonDocument document = new BsonDocument()
                .append("attribute", new BsonString("attr"))
                .append("type", new BsonString("array"))
                .append("of", new BsonString(of));
        if (hasContained) {
            document.append("contained", createBsonValue(valueCase, value));
        }
        if (hasMin) {
            document.append("min", new BsonInt32(1));
        }
        if (hasMax) {
            document.append("max", new BsonString("intAttribute"));
        }
        return document;
    }

    private JsonObject createFlightJsonObject(String type) {
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("intAttribute", new BsonInt32(3));
        bsonDocument.append("dateAttribute", new BsonString("2024-08-27T11:52:53.145Z"));
        BsonArray stringArray = createBsonArray(type);
        bsonDocument.append("attr", stringArray);
        return new JsonObject(bsonDocument.toJson());
    }

    private BsonValue createBsonValue(String valueCase, Object value) {
        return switch (valueCase) {
            case "number" -> new BsonInt32((int) value);
            case "date" -> new BsonString((String) value);
            default -> new BsonString((String) value);
        };
    }

    private BsonArray createBsonArray(String type) {
        return switch (type) {
            case "text" -> new BsonArray(
                    Arrays.asList(
                            new BsonString("text"),
                            new BsonString("text2"),
                            new BsonString("text3")
                    )
            );
            case "number" -> new BsonArray(
                    Arrays.asList(
                            new BsonInt32(23),
                            new BsonInt32(652),
                            new BsonInt32(77)
                    )
            );
            case "date" -> new BsonArray(
                    Arrays.asList(
                            new BsonString("2024-08-27T11:52:53.145Z"),
                            new BsonString("2024-08-30T11:52:53.145Z"),
                            new BsonString("2024-08-15T11:52:53.145Z")
                    )
            );
            default -> null;
        };
    }


}
