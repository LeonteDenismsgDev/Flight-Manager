package msg.flight.manager.services.flights.validation.date.typebased;

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

    public String validate(LocalDateTime attributeValue, String attribute) {
        String errMsg ="";
        if (before != null) {
            boolean isValid = dateValidator.isBefore(attributeValue, before);
            if(!isValid){
                errMsg += attribute + " should be before " + attributeValue.toString() + ".\n";
            }
        }
        if (after != null) {
            boolean isValid = dateValidator.isAfter(attributeValue, after);
            if(!isValid){
                errMsg += attribute + " should be after " + attributeValue.toString() +"\n";
            }
        }
        if (start != null && end != null) {
            boolean isValid = dateValidator.isInInterval(attributeValue, start, end);
            if(!isValid){
                errMsg += attribute + " should be between " + start.toString() + " - " + end.toString() +"\n";
            }
        }
        return errMsg;
    }
}
