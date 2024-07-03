package msg.flight.manager.persistence.annotations;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class EmailValidatorTest {
    private final EmailValidator emailValidator = new EmailValidator();

    @Test
    public void isValid_ReturnsFalse_whenMapIsNull() {
        Assertions.assertFalse(emailValidator.isValid(null, null));
    }

    @Test
    public void isValid_returnsFalse_whenMapIsEmpty() {
        Assertions.assertFalse(emailValidator.isValid(new HashMap<>(), null));
    }

    @Test
    public void isValid_returnsFalse_whenMapIsMissingEmail() {
        Assertions.assertFalse(emailValidator.isValid(Map.of("phone", "0386874"), null));
    }

    @Test
    public void isValid_returnsTrue_whenValidMap() {
        assert (emailValidator.isValid(Map.of("email", "test"), null));
    }
}
