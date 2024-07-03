package msg.flight.manager.controllers.user.auth;

import msg.flight.manager.ManagerApplication;
import msg.flight.manager.persistence.enums.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManagerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CUDUserControllerTest extends SpringControllerTest {

    @Test
    public void save_succeedWith200_whenAdminUser() throws Exception {
        String validRegisterUserJson = createValidJSON("Test", Role.CREW_ROLE);
        ResultActions result = mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isOk());
        String username = result.andReturn().getResponse().getContentAsString();
        userRepository.deleteUser(username);
    }

    @Test
    public void save_failsWith400_whenSaveData() throws Exception {
        String validRegisterUserJson = "{\"firstName\": \"John\"}";
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void save_failsWith401_whenUnauthorizedUser() throws Exception {
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("validRegisterUserJson"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void save_succeedWith200_whenSameCompanyManager() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        String validRegisterUserJson = createValidJSON("Test", Role.CREW_ROLE);
        ResultActions result = mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isOk());
        String username = result.andReturn().getResponse().getContentAsString();
        userRepository.deleteUser(username);
    }

    @Test
    public void save_failsWith403_whenWrongCompanyManager() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        String validRegisterUserJson = createValidJSON("OtherCompany", Role.FLIGHT_MANAGER_ROLE);
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void save_failsWith403_whenWrongRole() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        String validRegisterUserJson = createValidJSON("OtherCompany", Role.CREW_ROLE);
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void save_failsWith403_whenWrongRoleForAdminSave() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        String validRegisterUserJson = createValidJSON("Test", Role.ADMINISTRATOR_ROLE);
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void save_succeedWith200_whenValidRoleForAdminSave() throws Exception {
        String validRegisterUserJson = createValidJSON("Test", Role.ADMINISTRATOR_ROLE);
        ResultActions result = mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(validRegisterUserJson))
                .andExpect(status().isOk());
        String username = result.andReturn().getResponse().getContentAsString();
        userRepository.deleteUser(username);
    }

    @Test
    public void updateUserData_succeedWith200_whenSameUsernameUser() throws Exception {
        updateDBUserRole(Role.FLIGHT_MANAGER_ROLE);
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("testUser")))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserData_failsWith403_whenInvalidUser() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("otherUser")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateUserData_succeedsWith200_whenSameCompanyManager() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        userRepository.save(creteaDBUser("otherUser", "Test"));
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("otherUser")))
                .andExpect(status().isOk());
        userRepository.deleteUser("otherUser");
    }

    @Test
    public void updateUserData_failsWith403_whenOtherCompanyManager() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        userRepository.save(creteaDBUser("otherUser", "OtherTest"));
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("otherUser")))
                .andExpect(status().isForbidden());
        userRepository.deleteUser("otherUser");
    }

    @Test
    public void updateUserData_succeedsWith200_whenAdminUser() throws Exception {
        userRepository.save(creteaDBUser("otherUser", "OtherTest"));
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("otherUser"))).
                andExpect(status().isOk());
        userRepository.deleteUser("otherUser");
    }

    @Test
    public void updateUserData_failsWith400_whenInvalidUpdateData() throws Exception {
        mvc.perform(put("/flymanager/user/crew/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserData_failsWith401_whenNonAuthorizesUser() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(put("/flymanager/user/crew/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createValidUpdateJSON("testUser")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser_failsWith400_whenBadUpdateData() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        mvc.perform(put("/flymanager/user/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("testUser")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_failsWith400_whenInvalidUpdateData() throws Exception {
        mvc.perform(put("/flymanager/user/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createValidUpdateJSON("user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_failsWith401_whenNonAuthorizedUser() throws Exception {
        mvc.perform(put("/flymanager/user/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createValidUpdateJSON("user")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser_succeedsWith200_whenGoodCredentials() throws Exception {
        mvc.perform(put("/flymanager/user/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createAdminValidUpdateJSON("testUser")))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_failsWith400_whenUserNotFound() throws Exception {
        mvc.perform(put("/flymanager/user/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createAdminValidUpdateJSON("user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void toggleEnable_failsWith403_whenInvalidAuthority() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "anyUser"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void toggleEnable_failsWith403_whenOtherCompanyManager() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        userRepository.save(creteaDBUser("user", "OtherTest"));
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "user"))
                .andExpect(status().isForbidden());
        userRepository.deleteUser("user");
    }

    @Test
    public void toggleEnable_failsWith401_whenNonAuthorizeUser() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void toggleEnable_succeedsWith200_whenAdminUser() throws Exception {
        userRepository.save(creteaDBUser("user", "test"));
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "user"))
                .andExpect(status().isOk());
        userRepository.deleteUser("user");
    }

    @Test
    public void toggleEnable_succeedsWith200_whenSameManagerCompany() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        userRepository.save(creteaDBUser("user", "Test"));
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "user"))
                .andExpect(status().isOk());
        userRepository.deleteUser("user");
    }

    @Test
    public void toggleEnable_failsWith400_whenUserNotFound() throws Exception {
        mvc.perform(put("/flymanager/user/enable/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "user"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePassword_failsWih401_whenNonAuthorisedUser() throws Exception {
        mvc.perform(put("/flymanager/user/password/update")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updatePassword_failsWih403_whenInvalidUser() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(put("/flymanager/user/password/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content("{ \"username\":\"other\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePassword_failsWih400_whenInvalidUpdateData() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(put("/flymanager/user/password/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content("{ \"username\":\"testUser\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePassword_succeedsWih200_whenValidCredentials() throws Exception {
        mvc.perform(put("/flymanager/user/password/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content("{ \"username\":\"testUser\",\"password\":\"passA!23\"}"))
                .andExpect(status().isOk());
    }

    private String createAdminValidUpdateJSON(String username) {
        return String.format("{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"contactData\": {" +
                "    \"email\": \"john.doe@example.com\"" +
                "}," +
                "\"username\": \"%s\"," +
                "\"company\": \"NewValue\"," +
                "\"role\": \"CREW_ROLE\"" +
                "}", username);
    }

    private String createValidUpdateJSON(String username) {
        return String.format("{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"contactData\": {" +
                "    \"email\": \"john.doe@example.com\"" +
                "}," +
                "\"username\": \"%s\"" +
                "}", username);
    }

    private String createValidJSON(String company, Role role) {
        return String.format("{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"contactData\": {" +
                "    \"email\": \"john.doe@example.com\"" +
                "}," +
                "\"company\": \"%s\"," +
                "\"role\": \"%s\"" +
                "}", company, role.name());
    }
}
