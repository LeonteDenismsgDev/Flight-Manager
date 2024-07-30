package msg.flight.manager.services;

import jakarta.mail.MessagingException;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.CrewUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.UpdatePassword;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.TokenRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.persistence.repositories.WorkHoursRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.users.MailService;
import msg.flight.manager.services.users.UserService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.event.KeyValuePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private WorkHoursRepository workHoursRepository;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<UsersFilterOptions> filterOptionsCaptor;


    @Test
    public void save_returnsExpectedResponse_whenUseUserRegistrationOk() throws MessagingException {
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("encodedPassword");
        DBUser dbUser = createDBUser();
        Mockito.when(userRepository.save(Mockito.any(DBUser.class))).thenReturn(dbUser);
        Mockito.doNothing().when(mailService).sendUserCreatedMessage(Mockito.any(DBUser.class), Mockito.any(String.class));
        ResponseEntity<String> expected = ResponseEntity.ok("username");
        Assertions.assertEquals(expected, userService.save(createRegistrationUser()));
    }

    @Test
    public void updateUser_returnsExpectedResponse_whenUseNotFoun() throws MessagingException, IllegalAccessException {
        UpdateUserDto updateDto = new CrewUpdateUser();
        Mockito.when(userRepository.updateUser(updateDto)).thenReturn(0L);
        ResponseEntity<String> expected = new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected, userService.updateUser(updateDto));
    }

    @Test
    public void updateUser_returnsExpectedResponse_whenValidUpdateData() throws MessagingException, IllegalAccessException {
        UpdateUserDto updateDto = new CrewUpdateUser();
        Mockito.when(userRepository.updateUser(updateDto)).thenReturn(1L);
        ResponseEntity<String> expected = ResponseEntity.ok("Updated user");
        Assertions.assertEquals(expected, userService.updateUser(updateDto));
    }

    @Test
    public void updateUser_throwsIllegalArgumentException_whenUpdateFails() throws MessagingException, IllegalAccessException {
        UpdateUserDto updateDto = new CrewUpdateUser();
        Mockito.when(userRepository.updateUser(updateDto)).thenThrow(IllegalAccessError.class);
        Assertions.assertThrows(IllegalAccessError.class, () -> userService.updateUser(updateDto));
    }

    @Test
    public void updatePassword_returnsExpectedResponse_whenUseNotFound() throws MessagingException, IllegalAccessException {
        Mockito.when(userRepository.updatePassword(Mockito.any(),Mockito.any())).thenReturn(0L);
        ResponseEntity<String> expected = new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected, userService.updatePassword(new UpdatePassword()));
    }

    @Test
    public void updatePassword_returnsExpectedResponse_whenValidUpdateData() throws MessagingException, IllegalAccessException {
        Mockito.when(userRepository.updatePassword(Mockito.any(),Mockito.any())).thenReturn(1L);
        ResponseEntity<String> expected = ResponseEntity.ok("changed password");
        Assertions.assertEquals(expected, userService.updatePassword(new UpdatePassword()));
    }

    @Test
    public void toggleEnable_returnsExpectedResponse_whenUseNotFound() throws MessagingException, IllegalAccessException {
        Mockito.when(userRepository.toggleEnable("test")).thenReturn(null);
        ResponseEntity<String> expected =  new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected, userService.toggleEnable("test"));
    }

    @Test
    public void toggleEnable_returnsExpectedResponse_whenValidUpdateData() throws MessagingException, IllegalAccessException {
        Mockito.when(userRepository.toggleEnable("test")).thenReturn(new KeyValuePair("emailFound","ok"));
        ResponseEntity<String> expected = ResponseEntity.ok("The account has been disabled");
        Assertions.assertEquals(expected, userService.toggleEnable("test"));
    }

    @Test
    public void viewUserData_returnsExpectedResponse_whenUseNotFound() throws MessagingException, IllegalAccessException {
        Mockito.when(userRepository.findDataByUsername("test")).thenReturn(null);
        ResponseEntity<CrewUpdateUser> expected = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected, userService.viewUserData("test"));
    }

    @Test
    public void viewUserData_returnsExpectedResponse_whenValidUpdateData() throws MessagingException, IllegalAccessException {
        AdminUpdateUser updateUser = new AdminUpdateUser();
        Mockito.when(userRepository.findDataByUsername("test")).thenReturn(updateUser);
        ResponseEntity<AdminUpdateUser> expected = new ResponseEntity<>(updateUser, HttpStatus.OK);
        Assertions.assertEquals(expected, userService.viewUserData("test"));
    }

    @Test
    public void findAvailableUsers_returnsAList_whenValidUpdateData() throws MessagingException, IllegalAccessException {
        Mockito.when(workHoursRepository.findAvailableUsers(LocalDateTime.MIN,LocalDateTime.MAX,"startLocation")).thenReturn(new ArrayList<>());
        Assertions.assertEquals(new ArrayList<>(), userService.findAvailableUsers(LocalDateTime.MIN,LocalDateTime.MAX,"startLocation"));
    }

    @Test
    public void findUsers_callsUserRepositoryWithExpectedParameters_whenLoggedInUserAdmin() throws MessagingException, IllegalAccessException {
        // Set up the SecurityContext and Authentication
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser(Role.ADMINISTRATOR_ROLE));
        SecurityContextHolder.setContext(context);

        // Use matchers for all parameters of the filterUsers method
        Mockito.when(userRepository.filterUsers(
                Mockito.any(PageRequest.class),
                Mockito.any(UsersFilterOptions.class),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(new TableResult());

        // Call the service method
        userService.findUsers(new UsersFilterOptions("test", new ArrayList<>(), "fullName"), 2, 5);

        // Capture and verify the arguments passed to filterUsers
        Mockito.verify(userRepository).filterUsers(
                Mockito.eq(PageRequest.of(2, 5)),
                filterOptionsCaptor.capture(),
                Mockito.eq(Role.ADMINISTRATOR_ROLE.name()),
                Mockito.eq("test")
        );

        // Assertions to validate the captured value
        assert (filterOptionsCaptor.getValue().getCompany().contains(".*"));
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

    private SecurityUser createSecurityUser(Role role) {
        return SecurityUser.builder()
                .role(role.name())
                .company("test")
                .build();
    }
}
