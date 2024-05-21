package msg.flight.manager.persistence.dtos.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {
    private String password;
    @NotNull(message = "first name should not be  null")
    @Size(min = 3, message = "first name should have at last 3 letters")
    private String firstName;
    @NotNull(message = "last name should not be  null")
    @Size(min = 1, message = "last name should have at last 1 letters")
    private String lastName;
    private Map<String, String> contactData;
    private String address;
}
