package msg.flight.manager.services;

import jakarta.mail.MessagingException;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MailService mailService;
    @InjectMocks
    UserService userService;


    @Test
    public void save_ReturnsExpectedResponse_whenUseUserRegistrationOk() throws MessagingException {
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("encodedPassword");
        DBUser dbUser = createDBUser();
        Mockito.when(userRepository.save(Mockito.any(DBUser.class))).thenReturn(dbUser);
        Mockito.doNothing().when(mailService).sendUserCreatedMessage(Mockito.any(DBUser.class),Mockito.any(String.class));
        ResponseEntity<String> expected = ResponseEntity.ok("username");
        Assertions.assertEquals(expected, userService.save(createRegistrationUser()));
    }

    private DBUser createDBUser() {
        return DBUser.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactData(new HashMap<>())
                .address("address")
                .company("company")
                .username("username")
                .password("encodedPassword")
                .enabled(true)
                .role(Role.CREW_ROLE.name())
                .build();
    }

    private RegistrationUser createRegistrationUser() {
        return RegistrationUser
                .builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactData(new HashMap<>())
                .address("address")
                .company("company")
                .role(Role.CREW_ROLE.name())
                .build();
    }
}
