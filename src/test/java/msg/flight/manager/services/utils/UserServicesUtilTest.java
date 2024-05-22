package msg.flight.manager.services.utils;


import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;
import org.junit.jupiter.api.Test;

public class UserServicesUtilTest {

    public static final String FIRST_NAME = "User";
    public static final String LAST_NAME = "Test";

    @Test
    public void generateUsername_returnsExpectedUsername_whenRegisterUserGood() {
        String username = UserServicesUtil.generateUsername(createRegistrationUser());
        assert (username.matches("\\duseTcr"));
    }

    private RegistrationUser createRegistrationUser() {
        return RegistrationUser.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(Role.CREW_ROLE.name())
                .build();
    }


}
