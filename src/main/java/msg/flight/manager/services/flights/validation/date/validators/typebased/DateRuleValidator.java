package msg.flight.manager.services.flights.validation.date.validators.typebased;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
public class DateRuleValidator {
    private LocalDateTime before;
    private LocalDateTime after;
    private LocalDateTime start;
    private LocalDateTime end;
    private DateValidator dateValidator = new DateValidator();

    public boolean validate(LocalDateTime attributeValue) {
        boolean isValid = true;
        if (before != null) {
            isValid &= dateValidator.isBefore(attributeValue, before);
        }
        if (after != null) {
            isValid &= dateValidator.isAfter(attributeValue, after);
        }
        if (start != null && end != null) {
            isValid &= dateValidator.isInInterval(attributeValue, start, end);
        }
        return isValid;
    }
}
