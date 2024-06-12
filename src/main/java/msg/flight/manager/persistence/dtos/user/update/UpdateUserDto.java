package msg.flight.manager.persistence.dtos.user.update;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.annotations.HasEmailContact;

import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto extends CanUpdateDTO {
    @NotNull(message = "first name should not be  null")
    @Size(min = 3, message = "first name should have at last 3 letters")
    private String firstName;
    @NotNull(message = "last name should not be  null")
    @Size(min = 1, message = "last name should have at last 1 letters")
    private String lastName;
    @HasEmailContact(message = "email should not be null")
    private Map<String, String> contactData;
    private String address;
}
