package msg.flight.manager.services.utils;


import com.sun.jna.platform.win32.OaIdl;
import jakarta.validation.constraints.NotNull;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class UserServicesUtilTest {

    public static final String FIRST_NAME = "User";
    public static final String LAST_NAME = "Test";

    @Test
    public void generateUsername_returnsExpectedUsername_whenRegisterUserGood() {
        String username = UserServicesUtil.generateUsername(createRegistrationUser());
        assert (username.matches("\\duseTcr"));
    }

    @Test
    public void generatePassword_returnsExpectedPassword(){
        String result = UserServicesUtil.generatePassword();
        assert(result.matches("^password\\d+!"));
    }
    @Test
    public void getAccessRoles_returnsListWithAdministratorRole_whenAdministrator(){
        List<String> expected = List.of(Role.ADMINISTRATOR_ROLE.name());
        Assertions.assertEquals(expected,UserServicesUtil.getAccessRoles(Role.ADMINISTRATOR_ROLE.name()));
    }

    @Test
    public void getAccessRoles_returnsExpectedList_whenCompanyManager(){
        List<String> expected =  List.of(Role.ADMINISTRATOR_ROLE.name(), Role.COMPANY_MANAGER_ROLE.name());
        Assertions.assertEquals(expected,UserServicesUtil.getAccessRoles(Role.COMPANY_MANAGER_ROLE.name()));
    }

    @Test
    public void getAccessRoles_returnsEmptyList_whenCompanyManager(){
        assert (UserServicesUtil.getAccessRoles("").isEmpty());
    }

    private RegistrationUser createRegistrationUser() {
        return RegistrationUser.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .role(Role.CREW_ROLE.name())
                .build();
    }


}
