package msg.flight.manager.services.flights.validation.text;

import msg.flight.manager.services.flights.validation.template.text.typebased.StringValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class StringValidatorTest {

    private static final StringValidator stringValidator = new StringValidator();

    @Test
    public void isContainedIn_returnsTrue_whenTextAttributeIsPartOfTextReference(){
        Assertions.assertTrue(stringValidator.isContainedIn("text","textReference"));
    }

    @Test
    public void isContainedIn_returnsFalse_whenTextAttributeIsNotAPartOfTextReference(){
        Assertions.assertFalse(stringValidator.isContainedIn("reference","textReference"));
    }

    @Test
    public void isEqualWith_returnsTrue_whenTextAttributeIsSameWithTextReference(){
        Assertions.assertTrue(stringValidator.isEqualWith("textReference","textReference"));
    }

    @Test
    public void isEqualWith_returnsFalse_whenTextAttributeIsDifferentWithTextReference(){
        Assertions.assertFalse(stringValidator.isEqualWith("different","textReference"));
    }

    @Test
    public void isAPartOf_returnsTrue_whenTextAttributeIsPartOfArrayReference(){
        Assertions.assertTrue(stringValidator.isAPartOf("text", List.of("text","reference")));
    }

    @Test
    public void isAPartOf_returnsFalse_whenTextAttributeIsNotAPartOfArrayReference(){
        Assertions.assertFalse(stringValidator.isAPartOf("attribute", List.of("text","reference")));
    }
}
