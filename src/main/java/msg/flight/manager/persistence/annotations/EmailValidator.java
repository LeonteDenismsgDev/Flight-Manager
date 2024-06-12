package msg.flight.manager.persistence.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class EmailValidator implements ConstraintValidator<HasEmailContact, Map<String, String>> {
    @Override
    public boolean isValid(Map<String, String> map, ConstraintValidatorContext constraintValidatorContext) {
        return map != null && !map.isEmpty() && map.get("email") != null;
    }
}
