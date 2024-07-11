package msg.flight.manager.services.flights.validation.number.validators.typebased;

import lombok.*;

import java.util.List;

@Setter
@NoArgsConstructor
public class NumberRulesValidator<T extends Number & Comparable<T>>{
    private T min;
    private T mine;
    private T max;
    private T maxe;
    private T equals;
    private List<T> values;
    private  NumberValidator<T> validator = new NumberValidator<>();

    public boolean validate(T attributeValue){
        boolean isValid = true;
        if(min != null){
            isValid &= validator.isLesThan(attributeValue, min);
        }
        if(mine != null){
            isValid &= validator.isLessOrEqualsWith(attributeValue, mine);
        }
        if(max != null){
            isValid &= validator.isGreaterThan(attributeValue, max);
        }
        if(maxe != null){
            isValid &= validator.isGreaterOrEqualWith(attributeValue, maxe);
        }
        if(equals != null){
            isValid &= validator.isEqualWith(attributeValue, equals);
        }
        if(values != null){
            isValid &= validator.isContainedIn(attributeValue,values);
        }
        return  isValid;
    }
}
