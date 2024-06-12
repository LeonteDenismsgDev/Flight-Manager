package msg.flight.manager.services.utils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserServicesUtil {

    private static final String DEFAULT_PASSWORD = "password";
    private static final Random random = new Random();

    public static String generateUsername(@Valid RegistrationUser registrationUser) {
        int randomNumber = random.nextInt(10);
        String firstNameLetters = registrationUser.getFirstName().toLowerCase().substring(0, 3);
        char lastNameFirstLetter = registrationUser.getLastName().toUpperCase().charAt(0);
        String roleCode = Role.valueOf(registrationUser.getRole()).getValue();
        return randomNumber + firstNameLetters + lastNameFirstLetter + roleCode;
    }

    public static String generatePassword() {
        int randomVar = random.nextInt(1000);
        return DEFAULT_PASSWORD + randomVar + "!";
    }

    public static List<String> getAccessRoles(@NotNull String role) {
        try {
            Role.valueOf(role);
            if (role.equals(Role.ADMINISTRATOR_ROLE.name())) {
                return List.of(Role.ADMINISTRATOR_ROLE.name());
            } else {
                return List.of(Role.ADMINISTRATOR_ROLE.name(), Role.COMPANY_MANAGER_ROLE.name());
            }
        } catch (IllegalArgumentException ex) {
            return new ArrayList<>();
        }
    }
}
