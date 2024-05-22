package msg.flight.manager.services.utils;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;

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
}
