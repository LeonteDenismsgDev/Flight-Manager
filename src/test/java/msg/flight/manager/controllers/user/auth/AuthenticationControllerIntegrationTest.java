package msg.flight.manager.controllers.user.auth;

import msg.flight.manager.ManagerApplication;
import msg.flight.manager.persistence.enums.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManagerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerIntegrationTest extends SpringControllerTest {

    public static final String INVALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwam9oRGNyIiwiaWF0IjoxNzE2NTM2MjA3LCJleHAiOjE3MTY2MjI2MDd9.7KemKHyOuXjf7FLN36Xw8vbG07gLVWLYueDr1D0epuc";

    @Test
    public void login_succeedWith200_whenCorrectUser() throws Exception {
        String validLoginRequestJson = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + PASSWORD + "\"}";
        mvc.perform(post("/flymanager/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void login_failsWith401_whenBadCredentials() throws Exception {
        String invalidLoginRequestJson = "{\"username\": \"invalid_username\", \"password\": \"invalid_password\"}";

        mvc.perform(post("/flymanager/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginRequestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_failsWith400_whenBadRequest() throws Exception {
        String invalidLoginRequestJson = "{\"username\": \"invalid_username\"}";

        mvc.perform(post("/flymanager/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logout_failsWith401_whenNonAuthorisedUser() throws Exception {
        mvc.perform(delete("/flymanager/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_failsWith401_whenInvalidToken() throws Exception {
        mvc.perform(delete("/flymanager/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", INVALID_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_succeedWith200_whenValidToken() throws Exception {
        mvc.perform(delete("/flymanager/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void logout_failsWith401_whenRejectedToken() throws Exception {
        mvc.perform(delete("/flymanager/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
        mvc.perform(delete("/flymanager/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void adminCheck_failsWith403_whenUserHasNoAuthorities() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(get("/flymanager/auth/session/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void crewCheck_failsWith403_whenUserHasNoAuthorities() throws Exception {
        mvc.perform(get("/flymanager/auth/session/crew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void companyMangerCheck_failsWith403_whenUserHasNoAuthorities() throws Exception {
        mvc.perform(get("/flymanager/auth/session/company_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void flightMangerCheck_failsWith403_whenUserHasNoAuthorities() throws Exception {
        mvc.perform(get("/flymanager/auth/session/flight_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void adminCheck_succeedWith200_whenUserHasAuthorities() throws Exception {
        mvc.perform(get("/flymanager/auth/session/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void crewCheck_succeedWith200_whenUserHasAuthorities() throws Exception {
        updateDBUserRole(Role.CREW_ROLE);
        mvc.perform(get("/flymanager/auth/session/crew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void companyMangerCheck_succeedWith200_whenUserHasAuthorities() throws Exception {
        updateDBUserRole(Role.COMPANY_MANAGER_ROLE);
        mvc.perform(get("/flymanager/auth/session/company_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void flightMangerCheck_succeedWith200_whenUserHasAuthorities() throws Exception {
        updateDBUserRole(Role.FLIGHT_MANAGER_ROLE);
        mvc.perform(get("/flymanager/auth/session/flight_manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
