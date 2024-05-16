package msg.flight.manager.persistence.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@CompoundIndexes({
        @CompoundIndex(name = "company_role_index", def = "{'company': 1, 'role': 1}")
})
public class DBUser {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Map<String, String> contactData;
    private String address;
    private String company;
    private Boolean enabled;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String role;
}
