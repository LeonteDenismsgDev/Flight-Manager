package msg.flight.manager.services.flights.validation.extraction;

import msg.flight.manager.services.flights.validation.template.extraction.AttributeExtractor;
import org.bson.*;
import org.bson.json.JsonObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AttributeExtractorTest {
    private final AttributeExtractor attributeExtractor = Mockito.spy(AttributeExtractor.class);
    private static final List<BsonValue> bsonValues = List.of(
            new BsonString("text"),
            new BsonString("text2"),
            new BsonString("text3")
    );
    private static final BsonValue bsonValue = new BsonDocument("attribute", new BsonString("attribute"));

    @Test
    public void extractIntegerAttribute_returnsExpectedInteger_whenCalled() {
        Assertions.assertEquals(12, attributeExtractor.extractIntegerAttribute("integerAttribute", createJsonFlight()));
    }

    @Test
    public void extractIntegerAttribute_throwsInvalidAttributeException_whenNonCorrectAttributeLink() {
        assertThrows(RuntimeException.class, () -> attributeExtractor.extractIntegerAttribute("documentAttribute.textAttribute", createJsonFlight()));
    }

    @Test
    public void extractFloatAttribute_returnsExpectedFloat_whenCalled() {
        Assertions.assertEquals((float) 23.45, attributeExtractor.extractFloatAttribute("floatAttribute", createJsonFlight()));
    }

    @Test
    public void extractFloatAttribute_throwsInvalidAttributeException_whenNonCorrectAttributeLink() {
        assertThrows(RuntimeException.class, () -> attributeExtractor.extractFloatAttribute("documentAttribute.textAttribute", createJsonFlight()));
    }

    @Test
    public void extractTextAttribute_returnsExpectedText_whenCalled() {
        Assertions.assertEquals("textAttribute", attributeExtractor.extractTextAttribute("textAttribute", createJsonFlight()));
    }

    @Test
    public void extractTextAttribute_throwsInvalidAttributeException_whenNonCorrectAttributeLink() {
        assertThrows(RuntimeException.class, () -> attributeExtractor.extractTextAttribute("documentAttribute.numberAttribute", createJsonFlight()));
    }

    @Test
    public void extractArrayAttribute_returnsExpectedArray_whenCalled() {
        Assertions.assertEquals(bsonValues, attributeExtractor.extractArrayAttribute("arrayAttribute", createJsonFlight()));
    }

    @Test
    public void extractArrayAttribute_throwsInvalidAttributeException_whenNonCorrectAttributeLink() {
        assertThrows(RuntimeException.class, () -> attributeExtractor.extractArrayAttribute("documentAttribute.numberAttribute", createJsonFlight()));
    }

    @Test
    public void extractBsonValues_returnsExpectedBsonValue_whenCalled() {
        Assertions.assertEquals(bsonValue, attributeExtractor.extractBsonValue("bsonValue", createJsonFlight()));
    }

    @Test
    public void extractLocalDateTime_returnsExpectedDateTime_whenCalled() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2024-08-27T11:52:53.145Z");
        Assertions.assertEquals(offsetDateTime.toLocalDateTime(), attributeExtractor.extractLocalDateTimeAttribute("dateAttribute", createJsonFlight()));
    }

    @Test
    public void extractLocalDateTimeAttribute_throwsRuntimeException_whenWrongAttributeLink() {
        Assertions.assertThrows(RuntimeException.class, () -> attributeExtractor.extractLocalDateTimeAttribute("integerAttribute", createJsonFlight()));
    }

    @Test
    public void extractLocalDateTimeAttribute_throwsRuntimeException_whenAttributeExtractionFails() throws ClassNotFoundException {
        JsonObject jsonFlight = createJsonFlight();
        Assertions.assertThrows(RuntimeException.class, () -> attributeExtractor.extractLocalDateTimeAttribute("attributeLink", createJsonFlight()));
    }

    @Test
    public void extractAttributeValue_throwsRuntimeException_whenInvalidSimpleAttribute() throws ClassNotFoundException {
        Assertions.assertThrows(RuntimeException.class, () -> attributeExtractor.extractAttributeValue("invalidAttribute", createJsonFlight()));
    }

    @Test
    public void extractAttributeValue_throwsRuntimeException_whenInvalidDeepAttribute(){
        Assertions.assertThrows(RuntimeException.class, () -> attributeExtractor.extractAttributeValue("invalidAttribute.deepAttribute", createJsonFlight()));
    }

    private JsonObject createJsonFlight() {
        BsonDocument flightDoc = new BsonDocument()
                .append("integerAttribute", new BsonInt32(12))
                .append("floatAttribute", new BsonDouble(23.45))
                .append("textAttribute", new BsonString("textAttribute"))
                .append("arrayAttribute", new BsonArray(bsonValues))
                .append("dateAttribute", new BsonString("2024-08-27T11:52:53.145Z"))
                .append("bsonValue", bsonValue)
                .append("documentAttribute", new BsonDocument()
                        .append("textAttribute", new BsonString("testAttribute"))
                        .append("numberAttribute", new BsonInt32(23))
                );
        return new JsonObject(flightDoc.toJson());
    }


}
