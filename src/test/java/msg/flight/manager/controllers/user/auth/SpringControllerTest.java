package msg.flight.manager.controllers.user.auth;

import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.persistence.repositories.TokenRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class SpringControllerTest {
    @Autowired
    protected WebApplicationContext context;  // Changed to protected
    @Autowired
    protected UserRepository userRepository;  // Changed to protected
    @Autowired
    protected CompanyRepository companyRepository;
    @Autowired
    protected TokenRepository tokenRepository;  // Changed to protected
    protected DBUser testUser;  // Changed to protected
    protected MockMvc mvc;  // Changed to protected
    protected String token;  // Changed to protected
    @Autowired
    protected PasswordEncoder passwordEncoder;  // Changed to protected
    protected static final String PASSWORD = "password";  // Changed to protected

    @Before
    public void setup() throws Exception {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        companyRepository.save(new DBCompany("Test",20,null));
        companyRepository.save(new DBCompany("OtherTest",20,null));
        testUser = userRepository.save(createDBUser("testUser", "Test"));
        String validLoginRequestJson = "{\"username\": \"" + testUser.getUsername() + "\", \"password\": \"" + PASSWORD + "\"}";
        ResultActions result = mvc.perform(post("/flymanager/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validLoginRequestJson));
        String textResponse = result.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(textResponse);
        token = "Bearer " + jsonObject.get("token").toString();
    }

    @After
    public void clearSetup() {
        userRepository.deleteUser(testUser.getUsername());
        companyRepository.remove("Test");
         companyRepository.remove("OtherTest");
        tokenRepository.deleteToken(token);
    }

    protected DBUser createDBUser(String username, String company) {
        return DBUser.builder()
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode(PASSWORD))
                .role(Role.ADMINISTRATOR_ROLE.name())
                .enabled(true)
                .username(username)
                .contactData(Map.of("email", "test@yahoo.com"))
                .company(company).build();
    }

    protected void updateDBUserRole(Role role) throws IllegalAccessException {
        userRepository.updateUser(createAdminUpdateUser(role));
    }

    protected AdminUpdateUser createAdminUpdateUser(Role role) {
        AdminUpdateUser adminUpdateUser = new AdminUpdateUser(testUser.getCompany(), role.name());
        adminUpdateUser.setUsername(testUser.getUsername());
        adminUpdateUser.setFirstName(testUser.getFirstName());
        adminUpdateUser.setLastName(testUser.getLastName());
        return adminUpdateUser;
    }
}
