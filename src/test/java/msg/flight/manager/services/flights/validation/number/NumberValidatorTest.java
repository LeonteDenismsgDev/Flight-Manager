package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.template.number.validators.typebased.NumberValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class NumberValidatorTest {
    private static final NumberValidator<Integer> numberValidator = new NumberValidator<>();

    @Test
    public void isLessThan_isTrue_whenValueIsSmallerThanReference() {
        Assertions.assertTrue(numberValidator.isLessThan(1, 5));
    }

    @Test
    public void isLessThan_isFalse_whenValueIsGreaterThanReference() {
        Assertions.assertFalse(numberValidator.isLessThan(5, 1));
    }

    @Test
    public void isLessOrEqualWith_isTrue_whenValueIsEqualsWithReference() {
        Assertions.assertTrue(numberValidator.isLessOrEqualsWith(1, 1));
    }

    @Test
    public void isLessOrEqualWith_isFalse_whenValueIsGreaterThanReference() {
        Assertions.assertFalse(numberValidator.isLessOrEqualsWith(5, 1));
    }

    @Test
    public void isGreaterThan_isTrue_whenValueIsGreaterThanReference() {
        Assertions.assertTrue(numberValidator.isGreaterThan(5, 1));
    }

    @Test
    public void isGreaterThan_isFalse_whenValueIsSmallerThanReference() {
        Assertions.assertFalse(numberValidator.isGreaterThan(1, 5));
    }

    @Test
    public void isGreaterOrEqualWith_isTrue_wheValueIsEqualsWithReference() {
        Assertions.assertTrue(numberValidator.isGreaterOrEqualWith(1, 1));
    }

    @Test
    public void isGreaterOrEqualWith_isFalse_whenValueIsSmallerThanReference() {
        Assertions.assertFalse(numberValidator.isGreaterOrEqualWith(1, 5));
    }

    @Test
    public void isEqualWth_isTrue_whenValueIsEqualWithReference() {
        Assertions.assertTrue(numberValidator.isEqualWith(1, 1));
    }

    @Test
    public void isEqualWith_isFalse_whenValueDifferentFromReference() {
        Assertions.assertFalse(numberValidator.isEqualWith(1, 5));
    }

    @Test
    public void isContainedIn_isTrue_whenValueInArrayReference() {
        Assertions.assertTrue(numberValidator.isContainedIn(1, List.of(1, 5)));
    }

    @Test
    public void isContainedIn_isFalse_whenValueNotInArrayReference() {
        Assertions.assertFalse(numberValidator.isContainedIn(1, List.of(5)));
    }
}
