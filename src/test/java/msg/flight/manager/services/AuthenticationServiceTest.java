package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.user.auth.AuthenticationRequest;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationResponse;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.JWTService;
import msg.flight.manager.security.SecurityUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String TOKEN = "token";
    @Mock
    private JWTService jwtService;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthenticationManager manager;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void login_return_okResponse_whenUserExists() {
        Mockito.when(manager.authenticate(createUsernamePasswordAuthenticationToken())).thenReturn(null);
        Optional<SecurityUser> user = createOptionalSecurityUser();
        Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        Mockito.when(jwtService.generateToken(user.get())).thenReturn(TOKEN);
        ResponseEntity<AuthenticationResponse> response = authenticationService.login(createAuthenticaticationRequest());
        ResponseEntity<AuthenticationResponse> expected = ResponseEntity.ok(createAuthenticatioResponse());
        Assertions.assertEquals(expected, response);
    }

    @Test
    public void login_throwsAuthenticationException_whenNonexistentUser() {
        Mockito.when(manager.authenticate(createUsernamePasswordAuthenticationToken())).thenThrow(new AuthenticationException("error") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        });
        Assertions.assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(createAuthenticaticationRequest());
        });
    }

    private AuthenticationRequest createAuthenticaticationRequest() {
        return new AuthenticationRequest(USERNAME, PASSWORD);
    }

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
    }

    private Optional<SecurityUser> createOptionalSecurityUser() {
        SecurityUser securityUser = SecurityUser.builder().username(USERNAME).password(PASSWORD).role(Role.CREW_ROLE.name()).enabled(true).build();
        return Optional.of(securityUser);
    }

    private AuthenticationResponse createAuthenticatioResponse() {
        return AuthenticationResponse.builder().token(TOKEN).username(USERNAME).role(Role.CREW_ROLE.name()).build();
    }


}
