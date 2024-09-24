package msg.flight.manager.services.flights.validation.date;

import msg.flight.manager.services.flights.validation.template.date.attributebased.DateAttributeValidator;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class DateAttributeValidatorTest {

    @Test
    public void validate_returnsExpectedResponse_whenCalled() {
        DateAttributeValidator attributeValidator = new DateAttributeValidator();
        String expected = "attr should be before 2024-08-27T11:52:53.145\n" +
                "attr should be after 2024-08-27T11:52:53.145\n";
        Assertions.assertEquals(expected, attributeValidator.validate(createValidationRules(), createFlightJsonObject()));
    }

    private JsonObject createFlightJsonObject() {
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("attr", new BsonString("2024-08-27T11:52:53.145Z"));
        bsonDocument.append("dateAttribute", new BsonString("2024-08-27T11:52:53.145Z"));
        return new JsonObject(bsonDocument.toJson());
    }

    private BsonDocument createValidationRules() {
        return new BsonDocument()
                .append("attribute", new BsonString("attr"))
                .append("before", new BsonString("2024-08-27T11:52:53.145Z"))
                .append("after", new BsonString("dateAttribute"))
                .append("start", new BsonBoolean(true));
    }
}
