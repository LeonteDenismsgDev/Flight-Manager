package msg.flight.manager.persistence.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tokens")
public class DBToken {
    @Id
    private String token;
    private String username;
    private Boolean rejected;
    private LocalDateTime expirationDate;
    private LocalDateTime issuedAT;
}
