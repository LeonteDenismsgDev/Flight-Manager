package msg.flight.manager.persistence.annotations;

import jakarta.validation.Payload;
import msg.flight.manager.persistence.enums.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleValidatorTest {

    private RoleValidator roleValidator = new RoleValidator();

    @Before
    public void setup() {
        ValidRole validRole = new ValidRole() {
            @Override
            public String message() {
                return "Invalid role";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Enum<?>> enumClass() {
                return Role.class;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ValidRole.class;
            }
        };
        roleValidator.initialize(validRole);
    }

    @Test
    public void initialize_allowedNamesPopulated_whenCalled() {
        Set<String> expected = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toSet());
        Assertions.assertEquals(roleValidator.getAllowedNames(), expected);
    }

    @Test
    public void isValid_returnsFalse_whenInvalidRole() {
        Assertions.assertFalse(roleValidator.isValid("INVALID_ROLE", null));
    }

    @Test
    public void isValid_returnsFalse_whenNullRole() {
        Assertions.assertFalse(roleValidator.isValid(null, null));
    }

    @Test
    public void isValid_returnsTrue_whenValidRole() {
        assert (roleValidator.isValid(Role.CREW_ROLE.name(), null));
    }
}
