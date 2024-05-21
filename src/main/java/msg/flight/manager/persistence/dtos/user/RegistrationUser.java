package msg.flight.manager.persistence.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.annotations.ValidRole;
import msg.flight.manager.persistence.enums.Role;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUser {
    @NotNull(message = "first name should not be  null")
    @Size(min = 3, message = "first name should have at last 3 letters")
    private String firstName;
    @NotNull(message = "last name should not be  null")
    @Size(min = 1, message = "last name should have at last 1 letters")
    private String lastName;
    private Map<String, String> contactData;
    private String address;
    @NotNull(message = "company should not  be null")
    @NotBlank(message = "company should not be blank")
    private String company;
    @NotNull(message = "company should not  be null")
    @ValidRole(enumClass = Role.class, message = "invalid role")
    private String role;
}
