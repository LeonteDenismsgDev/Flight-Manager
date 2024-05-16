package msg.flight.manager.persistence.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUser {
    private String firstName;
    private String lastName;
    private Map<String, String> contactData;
    private String address;
    private String company;
    private String role;
}
