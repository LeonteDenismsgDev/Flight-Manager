package msg.flight.manager.persistence.dtos.user.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "username should not be null")
    String username;
    @NotNull(message = "password should not be null")
    String password;
}
