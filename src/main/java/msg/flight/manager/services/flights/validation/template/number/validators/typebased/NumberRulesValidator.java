package msg.flight.manager.services.flights.validation.template.number.validators.typebased;

import lombok.*;

import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumberRulesValidator<T extends Number & Comparable<T>>{
    private T min;
    private T mine;
    private T max;
    private T maxe;
    private T equals;
    private List<T> values;
    private  NumberValidator<T> validator = new NumberValidator<>();

    public String validate(T attributeValue, String attribute){
        String errMessage = "";
        if(min != null){
           boolean isValid = validator.isLessThan(attributeValue, min);
           if(!isValid){
               errMessage += attribute + " should be less than " + min +"\n";
            }
        }
        if(mine != null){
            boolean isValid = validator.isLessOrEqualsWith(attributeValue, mine);
            if(!isValid){
                errMessage += attribute + " should be less or equal with " + mine +"\n";
            }
        }
        if(max != null){
            boolean isValid = validator.isGreaterThan(attributeValue, max);
            if(!isValid){
                errMessage += attribute + " should be greater than " + max +"\n";
            }
        }
        if(maxe != null){
            boolean isValid = validator.isGreaterOrEqualWith(attributeValue, maxe);
            if(!isValid){
                errMessage += attribute + " should be greater or equal with " + maxe +"\n";
            }
        }
        if(equals != null){
            boolean isValid = validator.isEqualWith(attributeValue, equals);
            if(!isValid){
                errMessage += attribute + " should be equal with " + equals +"\n";
            }
        }
        if(values != null){
            boolean isValid = validator.isContainedIn(attributeValue,values);
            if(!isValid){
                errMessage += attribute + " should be a part of " + values.toString() +"\n";
            }
        }
        return  errMessage;
    }
}
