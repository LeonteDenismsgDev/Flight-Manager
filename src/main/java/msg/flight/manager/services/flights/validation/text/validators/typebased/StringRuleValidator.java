package msg.flight.manager.services.flights.validation.text.validators.typebased;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
public class StringRuleValidator {
    private String containedIn;
    private String equalWith;
    private List<String> values;
    private StringValidator validator = new StringValidator();

    public boolean validate(String attributeValue){
        boolean isValid = true;
        if(containedIn != null){
            isValid &= validator.isContainedIn(attributeValue,containedIn);
        }
        if(equalWith != null){
            isValid &= validator.isEqualWith(attributeValue,equalWith);
        }
        if(values != null){
            isValid &= validator.isAPartOf(attributeValue,values);
        }
        return isValid;
    }

}


