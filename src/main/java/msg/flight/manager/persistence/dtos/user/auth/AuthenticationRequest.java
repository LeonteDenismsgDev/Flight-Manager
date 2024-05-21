package msg.flight.manager.persistence.dtos.user.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "username should not be null")
    @NotBlank(message = "username should not be blank")
    @Pattern(regexp = "\\d[a-z]{3}[A-Z][a-z]{2}", message = "invalid username")
    String username;
    @NotNull(message = "username should not be null")
    @NotBlank(message = "username should not be blank")
    String password;
}
