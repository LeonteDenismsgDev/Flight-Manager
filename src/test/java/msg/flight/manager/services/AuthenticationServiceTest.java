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
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.http.HttpStatus;
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
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private JWTService service;
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

    @Test
    public void logout_returnsBadRequestResponse_whenNonexistentUser() {
        String token = "Bearer invalidToken";
        Mockito.when(jwtService.rejectToken(token.substring(7)))
                        .thenReturn(0L);
        ResponseEntity<String> expected = new ResponseEntity<String>("still connected", HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected,authenticationService.logout(token));
    }

    @Test
    public void logout_returnsOkRequestResponse_whenExistentUser() {
        String token = "Bearer goodToken";
        Mockito.when(jwtService.rejectToken(token.substring(7)))
                .thenReturn(7L);
        ResponseEntity<String> expected = new ResponseEntity<String>("disconnected", HttpStatus.OK);
        Assertions.assertEquals(expected,authenticationService.logout(token));
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
