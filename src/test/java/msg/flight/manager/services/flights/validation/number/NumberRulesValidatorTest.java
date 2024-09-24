package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.template.number.validators.typebased.NumberRulesValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.typebased.NumberValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class NumberRulesValidatorTest {
    @Test
    public void validate_returnsExpectedResponse_whenAllNullRules() {
        NumberRulesValidator<Integer> numberValidator = new NumberRulesValidator<>();
        String expected = "";
        Assertions.assertEquals(expected, numberValidator.validate(23, "attr"));
    }

    @Test
    public void validate_returnsExpectedResponse_whenAllRulesAreValidated() {
        NumberRulesValidator<Integer> numberValidator = new NumberRulesValidator<>(50, 50, 2, 2, 20, List.of(20, 10, 2, 45), new NumberValidator<>());
        String expected = "";
        Assertions.assertEquals(expected, numberValidator.validate(20, "attr"));
    }

    @Test
    public void validate_returnsExpectedResponse_whenAllRulesAreBroke(){
        NumberRulesValidator<Integer> numberValidator = new NumberRulesValidator<>(2, 2, 50, 50, 10, List.of(25, 15, 2, 45), new NumberValidator<>());
        String expected = "attr should be less than 2\n" +
                "attr should be less or equal with 2\n" +
                "attr should be greater than 50\n" +
                "attr should be greater or equal with 50\n" +
                "attr should be equal with 10\n" +
                "attr should be a part of [25, 15, 2, 45]\n";
        Assertions.assertEquals(expected, numberValidator.validate(20, "attr"));
    }
}
