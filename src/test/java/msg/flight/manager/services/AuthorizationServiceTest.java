package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.dtos.user.update.CanUpdateDTO;
import msg.flight.manager.persistence.dtos.user.update.CrewUpdateUser;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.SecurityUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthorizationService authorizationService = new AuthorizationService();
    private Authentication authentication;

    @Before
    public void setup() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void canRegisterUser_returnsFalse_whenAdminRegistrationUserAndNonAdminLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.CREW_ROLE.name())));
        RegistrationUser registrationUser = createRegistrationUser(Role.ADMINISTRATOR_ROLE.name(), "test");
        Assertions.assertFalse(authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canRegisterUser_returnsTrue_whenAdminRegistrationUserAndAdminLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ADMINISTRATOR_ROLE.name())));
        RegistrationUser registrationUser = createRegistrationUser(Role.ADMINISTRATOR_ROLE.name(), "test");
        assert (authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canRegisterUser_returnsTrue_whenCrewRegistrationUserAndAdminLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ADMINISTRATOR_ROLE.name())));
        RegistrationUser registrationUser = createRegistrationUser(Role.CREW_ROLE.name(), "test");
        assert (authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canRegisterUser_returnsFalse_whenCrewRegistrationUserAndNonAdminLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.CREW_ROLE.name())));
        RegistrationUser registrationUser = createRegistrationUser(Role.CREW_ROLE.name(), "test");
        Assertions.assertFalse(authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canRegisterUser_returnsFalse_whenCrewRegistrationUserAndDifferentCompanyManagerLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        RegistrationUser registrationUser = createRegistrationUser(Role.CREW_ROLE.name(), "otherCompany");
        Assertions.assertFalse(authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canRegisterUser_returnsFalse_whenCrewRegistrationUserAndDSameCompanyManagerLogInUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        RegistrationUser registrationUser = createRegistrationUser(Role.CREW_ROLE.name(), "test");
        assert (authorizationService.canRegisterUser(registrationUser));
    }

    @Test
    public void canUpdatePassword_returnsFalse_whenDifferentCrewUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.CREW_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        CanUpdateDTO updateDTO = crewUpdateUser("otherUser");
        Assertions.assertFalse(authorizationService.canUpdatePassword(updateDTO));
    }

    @Test
    public void canUpdatePassword_returnsTrue_whenSameCrewUser() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.FLIGHT_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        CanUpdateDTO updateDTO = crewUpdateUser("testUser");
        assert (authorizationService.canUpdatePassword(updateDTO));
    }

    @Test
    public void canUpdatePassword_returnsTrue_whenSameCompanyManager() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(createSecurityUser("test")));
        CanUpdateDTO updateDTO = crewUpdateUser("testUser");
        assert (authorizationService.canUpdatePassword(updateDTO));
    }

    @Test
    public void canUpdatePassword_returnsFalse_whenOtherCompanyManager() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(createSecurityUser("otherCompany")));
        CanUpdateDTO updateDTO = crewUpdateUser("testUser");
        Assertions.assertFalse(authorizationService.canUpdatePassword(updateDTO));
    }

    @Test
    public void canViewUserData_returnsTrue_whenAdministrator() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ADMINISTRATOR_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        assert (authorizationService.canViewUserData("testUser"));
    }

    @Test
    public void canUpdatePassword_returnsTrue_whenAdministrator() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ADMINISTRATOR_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        CanUpdateDTO updateDTO = crewUpdateUser("testUser");
        assert (authorizationService.canUpdatePassword(updateDTO));
    }

    @Test
    public void canDisableUser_returnsTrue_whenSameCompanyManger() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(createSecurityUser("test")));
        assert (authorizationService.canDisableUser("testUser"));
    }

    @Test
    public void canDisableUser_returnsFalse_whenOtherCompanyManger() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.COMPANY_MANAGER_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(createSecurityUser("other test")));
        Assertions.assertFalse(authorizationService.canDisableUser("testUser"));
    }

    @Test
    public void canDisableUser_returnsTrue_whenAdministrator() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ADMINISTRATOR_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        assert (authorizationService.canDisableUser("testUser"));
    }

    @Test
    public void canDisableUser_returnsFalse_whenCrew() {
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.CREW_ROLE.name())));
        Mockito.when(authentication.getPrincipal()).thenReturn(createSecurityUser("test"));
        Assertions.assertFalse(authorizationService.canDisableUser("testUser"));
    }


    private RegistrationUser createRegistrationUser(String role, String company) {
        return RegistrationUser.builder()
                .role(role)
                .company(company)
                .build();
    }

    private SecurityUser createSecurityUser(String company) {
        return SecurityUser.builder()
                .company(company)
                .username("testUser")
                .build();
    }

    private CrewUpdateUser crewUpdateUser(String username) {
        CrewUpdateUser crewUpdateUser = new CrewUpdateUser();
        crewUpdateUser.setUsername(username);
        return crewUpdateUser;
    }
}
