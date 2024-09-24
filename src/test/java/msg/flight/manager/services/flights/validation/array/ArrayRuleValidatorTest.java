package msg.flight.manager.services.flights.validation.array;

import msg.flight.manager.services.flights.validation.template.array.typebased.ArrayRuleValidator;
import org.bson.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class ArrayRuleValidatorTest {
    @Test
    public void validate_returnsExpectedResponse_whenCalledWithAllNonNullRules() {
        ArrayRuleValidator<BsonString> arrayRuleValidator = new ArrayRuleValidator<>(new BsonString("test"), 2, 7);
        String expected = "textAttribute should contain \"test\"\ntextAttribute length should be less than 2\ntextAttribute length should be greater than 7\n";
        List<BsonString> array = List.of(
                new BsonString("text"),
                new BsonString("text1"),
                new BsonString("text2")
        );
        Assertions.assertEquals(expected, arrayRuleValidator.validate(array, "textAttribute"));
    }

    @Test
    public void validate_returnsExpectedResponse_whenCalledWithAllRulesNull() {
        ArrayRuleValidator<BsonString> arrayRuleValidator = new ArrayRuleValidator<>();
        String expected = "";
        List<BsonString> array = new ArrayList<>();
        Assertions.assertEquals(expected, arrayRuleValidator.validate(array, "textAttribute"));
    }

    @Test
    public void validate_returnsEmptyResponse_whenMapVerifiesRules() {
        ArrayRuleValidator<BsonString> arrayRuleValidator = new ArrayRuleValidator<>(new BsonString("text"), 5, 1);
        String expected = "";
        List<BsonString> array = List.of(
                new BsonString("text"),
                new BsonString("text1"),
                new BsonString("text2")
        );
        Assertions.assertEquals(expected, arrayRuleValidator.validate(array, "textAttribute"));
    }

    @Test
    public void toJsonString_returnsExpectedResponse_whenCalled() throws ParseException {
        String dateString = "2022-01-01T12:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date customDate = dateFormat.parse(dateString);
        BsonDateTime bsonDate = new BsonDateTime(customDate.getTime());
        BsonDocument bsonDocument = new BsonDocument();
        bsonDocument.append("attribute", new BsonString("text"));
        bsonDocument.append("attribute2", new BsonString("text"));
        Assertions.assertEquals("\"text\"", ArrayRuleValidator.toJsonString(new BsonString("text")));
        Assertions.assertEquals("23", ArrayRuleValidator.toJsonString(new BsonInt32(23)));
        Assertions.assertEquals("2345", ArrayRuleValidator.toJsonString(new BsonInt64(2345)));
        Assertions.assertEquals("23.45", ArrayRuleValidator.toJsonString(new BsonDouble(23.45)));
        Assertions.assertEquals("\"2022-01-01T12:00:00.000Z\"", ArrayRuleValidator.toJsonString(bsonDate));
        Assertions.assertEquals("[\"text\", \"text2\"]", ArrayRuleValidator.toJsonString(new BsonArray(
                Arrays.asList(
                        new BsonString("text"),
                        new BsonString("text2")
                )
        )));
        Assertions.assertEquals("{\"attribute\": \"text\", \"attribute2\": \"text\"}",ArrayRuleValidator.toJsonString( bsonDocument));
        Assertions.assertEquals("",ArrayRuleValidator.toJsonString(new BsonBoolean(true)));
    }
}
