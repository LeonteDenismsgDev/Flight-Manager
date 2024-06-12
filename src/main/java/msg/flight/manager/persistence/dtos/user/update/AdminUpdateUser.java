package msg.flight.manager.persistence.dtos.user.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.annotations.ValidRole;
import msg.flight.manager.persistence.enums.Role;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminUpdateUser extends UpdateUserDto {
    @NotNull(message = "company should not  be null")
    @NotBlank(message = "company should not be blank")
    private String company;
    @NotNull(message = "company should not  be null")
    @ValidRole(enumClass = Role.class, message = "invalid role")
    private String role;
}
