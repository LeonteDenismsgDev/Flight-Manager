package msg.flight.manager.security;

import msg.flight.manager.persistence.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JWTServiceTest {

    private final JWTService jwtService = new JWTService();

    @Test
    public void generateToken_returnsToken_whenCalled() {
        UserDetails userDetails = new SecurityUser("username", "password", true, Role.CREW_ROLE.name());
        String token = jwtService.generateToken(userDetails);
        assertTrue(token != null && !token.isEmpty());
    }

    @Test
    public void extractUsername_returnsUsername_whenCalled() {
        UserDetails userDetails = new SecurityUser("username", "password", true, Role.CREW_ROLE.name());
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("username", extractedUsername);
    }

    @Test
    public void isTokenValid_returnsTrue_whenCalled() {
        UserDetails userDetails = new SecurityUser("username", "password", true, Role.CREW_ROLE.name());
        String token = jwtService.generateToken(userDetails);
        assert (jwtService.isTokenValid(token, userDetails));
    }
}
