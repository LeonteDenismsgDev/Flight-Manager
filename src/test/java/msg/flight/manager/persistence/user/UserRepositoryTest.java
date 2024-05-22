package msg.flight.manager.persistence.user;

import msg.flight.manager.ManagerApplication;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.SecurityUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Optional;

@DataMongoTest
@ContextConfiguration(classes = {ManagerApplication.class})
public class UserRepositoryTest {
    public static final String USERNAME = "username1";
    public static final String PASSWORD = "password";
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_returnsSecurityUser_whenUserExists() {
        DBUser user = userRepository.save(createDBUser());
        Optional<SecurityUser> securityUserOption = userRepository.findByUsername(user.getUsername());
        SecurityUser securityUser = createSecurityUser();
        assert (securityUserOption.isPresent());
        Assertions.assertEquals(securityUserOption.get(), securityUser);
        userRepository.deleteById(user.getId());
    }

    @Test
    public void findByUsername_returnsEmptyOptional_whenNonexistentUser() {
        Optional<SecurityUser> securityUserOption = userRepository.findByUsername("Test154323e");
        assert (securityUserOption.isEmpty());
    }

    private SecurityUser createSecurityUser() {
        return SecurityUser.builder().username(USERNAME)
                .password(PASSWORD)
                .enabled(true)
                .role(Role.CREW_ROLE.name())
                .build();
    }

    private DBUser createDBUser() {
        return DBUser.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .role(Role.CREW_ROLE.name())
                .username(USERNAME)
                .company("Company")
                .password(PASSWORD)
                .enabled(true)
                .contactData(new HashMap<>())
                .build();
    }
}
