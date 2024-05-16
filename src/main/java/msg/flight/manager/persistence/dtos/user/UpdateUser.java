package msg.flight.manager.persistence.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, String> contactData;
    private String address;
}
