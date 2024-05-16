package msg.flight.manager.services.utils;

import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;

import java.util.Random;

public class UserServicesUtil {

    private static final String DEFAULT_PASSWORD = "password";
    private static final Random random = new Random();
    private static final StringBuilder builder = new StringBuilder();

    public static String generateUsername(RegistrationUser registrationUser) {
        int randomNumber = random.nextInt(10);
        String firstNameLetters = registrationUser.getLastName().toLowerCase().substring(0, 3);
        char lastNameFirstLetter = registrationUser.getLastName().charAt(0);
        String roleCode = Role.valueOf(registrationUser.getRole()).getValue();
        return randomNumber + firstNameLetters + lastNameFirstLetter + roleCode;
    }

    public static String generatePassword() {
        int password = random.nextInt(1000);
        return DEFAULT_PASSWORD + password + "!";
    }
}
