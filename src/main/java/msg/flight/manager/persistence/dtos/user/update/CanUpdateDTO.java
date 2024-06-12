package msg.flight.manager.persistence.dtos.user.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanUpdateDTO {
    @NotNull(message = "username should not be null")
    @NotBlank(message = "username should not be null")
    @Id
    private String username;
}
