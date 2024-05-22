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
public class UserControllerTest {

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
    public void save_succeedWith200_whenCorrectUser() throws Exception {
        String validRegisterUserJson = "{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"contactData\": {" +
                "    \"email\": \"john.doe@example.com\"" +
                "}," +
                "\"company\": \"Acme Corporation\"," +
                "\"role\": \"CREW_ROLE\"" +
                "}";
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterUserJson))
                .andExpect(status().isOk());
    }

    @Test
    public void save_failsWith400_whenInvalidUser() throws Exception {
        String validRegisterUserJson = "{" +
                "\"firstName\": \"John\"," +
                "\"lastName\": \"Doe\"," +
                "\"contactData\": {" +
                "    \"email\": \"john.doe@example.com\"" +
                "}," +
                "\"company\": \"Acme Corporation\"," +
                "}";
        mvc.perform(post("/flymanager/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRegisterUserJson))
                .andExpect(status().isBadRequest());
    }
}
