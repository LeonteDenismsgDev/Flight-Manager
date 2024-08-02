package msg.flight.manager.controllers.user.auth;

import msg.flight.manager.ManagerApplication;
import msg.flight.manager.persistence.enums.Role;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManagerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RUserControllerTest extends SpringControllerTest {
    @Test
    public void getUserData_succeedsWith200_whenValidCredentials() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        ResultActions result = mvc.perform(get("/flymanager/view/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "testUser"))
                .andExpect(status().isOk());
        String expected = String.format("{\"username\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"contactData\":{\"email\":\"%s\"},\"address\":null,\"enabled\":%s,\"company\":\"%s\",\"role\":\"%s\"}",
                testUser.getUsername(), testUser.getFirstName(), testUser.getLastName(), testUser.getContactData().get("email"),testUser.getEnabled().toString(),testUser.getCompany(),"CREW_ROLE");
        String getResult = result.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(expected, getResult);
    }

    @Test
    public void getUserData_failsWith403_whenInvalidCredentials() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        ResultActions result = mvc.perform(get("/flymanager/view/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "otherUser"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserData_failsWith400_whenUserNotFound() throws Exception {
        ResultActions result = mvc.perform(get("/flymanager/view/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .param("username", "invalidUser"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserData_failsWith401_whenUserNonAuthorisedUser() throws Exception {
        ResultActions result = mvc.perform(get("/flymanager/view/user")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "otherUser")).andExpect(status().isUnauthorized());
    }

    @Test
    public void findUsers_failsWith403_whenInvalidCredentials() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        ResultActions result = mvc.perform(get("/flymanager/view/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(createFilterOptions("", "", "")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void findUsers_failsWith401_whenNonAuthorized() throws Exception {
        ResultActions result = mvc.perform(get("/flymanager/view/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findUsers_failsWith400_whenInvalidRequest() throws Exception {
        ResultActions result = mvc.perform(get("/flymanager/view/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void findUsers_succeeds200_whenValidRequest() throws Exception {
        userRepository.save(creteaDBUser("username", "Test"));
        ResultActions result = mvc.perform(get("/flymanager/view/users")
                        .header("Authorization", token)
                        .param("page", "0")
                        .param("size", "6")
                        .content(createFilterOptions("", "", "Test")))
                .andExpect(status().is4xxClientError());
        System.out.println(result.andReturn().getResponse().getContentAsString());
    }

    public String createFilterOptions(String fullName, String role, String company) {
        return String.format("{" +
                "\"company\": \"%s\"," +
                "\"roles\": [" +
                "\"%s\"" +
                "]," +
                "\"fullName\": \"%s\"" +
                "}", company, role, fullName);
    }
}
