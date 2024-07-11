package msg.flight.manager.services.flights.validation.array.validator.typebased;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ArrayRuleValidator<T> {
    private T contained;
    private Integer max;
    private Integer min;

    public boolean validate(List<T> attributeValue) {
        boolean isValid = true;
        if (contained != null) {
            isValid &= attributeValue.contains(contained);
        }
        if (max != null) {
            isValid &= attributeValue.size() < max;
        }
        if (min != null) {
            isValid &= attributeValue.size() > min;
        }
        return isValid;
    }
}
