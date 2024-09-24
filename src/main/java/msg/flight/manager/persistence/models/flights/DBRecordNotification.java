package msg.flight.manager.persistence.models.flights;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "flightNotifications")
public class DBRecordNotification {
    @Id
    private String id;
    private String flightNotificationType;
    private LocalDateTime sendDate;
    private String email;
    private String recurrenceId;
    private Integer recurrenceNumber;
    private static final String type = "record";

    @Builder
    public DBRecordNotification(@NotNull String flightNotificationType, @NotNull LocalDateTime sendDate, @NotNull String email,
                                String recurrenceId, Integer recurrenceNumber) {
        this.flightNotificationType = flightNotificationType;
        this.sendDate = sendDate;
        this.email = email;
        this.recurrenceId = recurrenceId;
        this.recurrenceNumber = recurrenceNumber;
    }
}
