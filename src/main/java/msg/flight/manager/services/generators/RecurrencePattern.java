package msg.flight.manager.services.generators;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class RecurrencePattern {
    private String recurrencePattern;

    public String getRecurrenceTimeUnit() {
        Pattern pattern = Pattern.compile(".*\\d+\\s*(.*)");
        Matcher matcher = pattern.matcher(recurrencePattern);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Invalid pattern");
    }

    public int getRecurrenceNumber() {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(recurrencePattern);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new RuntimeException("Invalid pattern");
    }

    public long timeDifference(LocalDateTime recurrenceStart, LocalDateTime filterStart) {
        return switch (getRecurrenceTimeUnit()) {
            case "mo" -> ChronoUnit.MONTHS.between(recurrenceStart, filterStart);
            case "y" -> ChronoUnit.YEARS.between(recurrenceStart, filterStart);
            case "m" -> ChronoUnit.MINUTES.between(recurrenceStart, filterStart);
            case "d" -> ChronoUnit.DAYS.between(recurrenceStart,filterStart);
            default -> throw new RuntimeException("Invalid pattern");
        };
    }

    public LocalDateTime addTimeUnit(LocalDateTime recurrenceStart) {
        return switch (getRecurrenceTimeUnit()) {
            case "mo" -> recurrenceStart.plusMonths(getRecurrenceNumber());
            case "y" -> recurrenceStart.plusYears(getRecurrenceNumber());
            case "m" -> recurrenceStart.plusMinutes(getRecurrenceNumber());
            case "d" -> recurrenceStart.plusDays(getRecurrenceNumber());
            default -> throw new RuntimeException("Invalid pattern");
        };
    }
}
