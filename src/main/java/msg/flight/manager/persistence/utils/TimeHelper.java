package msg.flight.manager.persistence.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TimeHelper {
    private int hour;
    private int minute;
    private int day;
    private int month;
    private int year;
}
