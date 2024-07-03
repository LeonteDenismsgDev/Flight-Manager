package msg.flight.manager.persistence.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    private Set<String> allowedNames;

    @Override
    public void initialize(ValidRole constraintAnnotation) {

        allowedNames = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && allowedNames.contains(s);
    }

}
