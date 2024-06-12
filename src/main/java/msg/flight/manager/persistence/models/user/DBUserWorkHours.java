package msg.flight.manager.persistence.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "workHours")
public class DBUserWorkHours {
    @Id
    private String id;
    @Indexed(unique = true)
    private String user;
    private String lastLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
