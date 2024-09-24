package msg.flight.manager.persistence.models.flights;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "flightNotifications")
public class DBMessageNotification {
    @Id
    private String id;
    private LocalDateTime sendDate;
    private String email;
    private String message;
    private static final String type = "message";

    @Builder
    public DBMessageNotification(@NotNull LocalDateTime sendDate, String email,
                          @NotNull String message) {

        this.sendDate = sendDate;
        this.email = email;
        this.message = message;
    }
}
