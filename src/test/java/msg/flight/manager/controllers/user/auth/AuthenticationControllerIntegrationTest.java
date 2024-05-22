package msg.flight.manager.controllers.user.auth;

import msg.flight.manager.ManagerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManagerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void login_succeedWith200_whenCorrectUser() throws Exception {
        String validLoginRequestJson = "{\"username\": \"8goeGcr\", \"password\": \"password115!\"}";

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
}
