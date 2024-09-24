package msg.flight.manager.services.flights.validation.text;

import msg.flight.manager.services.flights.validation.template.text.typebased.StringRuleValidator;
import msg.flight.manager.services.flights.validation.template.text.typebased.StringValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class StringRuleValidatorTest {

    @Test
    public void validate_returnsExpectedResponse_whenAllRulesAreNull(){
        String expected = "";
        StringRuleValidator ruleValidator = new StringRuleValidator();
        Assertions.assertEquals(expected,ruleValidator.validate("textReference","attributeLink"));
    }

    @Test
    public void validate_returnsExpectedResponse_whenAllRulesAreValidated(){
        String expected = "";
        StringRuleValidator ruleValidator = new StringRuleValidator("textReference","text", List.of("text","reference"),new StringValidator());
        Assertions.assertEquals(expected,ruleValidator.validate("text","attribute"));
    }

    @Test
    public void validate_returnsExpectedResponse_whenAllRulesAreBroken(){
        String expected = "attribute should be contained in 'containedIn'\n" +
                "attribute should be equal with 'equalWith'\n" +
                "attribute should be a part of : [textReference, attributeReference]\n";
        StringRuleValidator ruleValidator = new StringRuleValidator("containedIn","equalWith",List.of("textReference","attributeReference"),new StringValidator());
        Assertions.assertEquals(expected,ruleValidator.validate("text","attribute"));
    }

}
