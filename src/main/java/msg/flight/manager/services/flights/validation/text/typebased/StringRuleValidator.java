package msg.flight.manager.services.flights.validation.text.typebased;

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

    public String validate(String attributeValue, String attributeLink){
        String validationError = "";
        if(containedIn != null){
            boolean isValid = validator.isContainedIn(attributeValue,containedIn);
            if(!isValid){
                validationError += attributeLink + " should be contained in '" + containedIn +"'\n";
            }
        }
        if(equalWith != null){
            boolean isValid = validator.isEqualWith(attributeValue,equalWith);
            if(!isValid){
                validationError += attributeLink + " should be equal with '" + equalWith +"'\n";
            }
        }
        if(values != null){
            boolean isValid = validator.isAPartOf(attributeValue,values);
            if(!isValid){
                validationError += attributeLink + " should be a part of : " + values.toString() +"\n";
            }
        }

        return validationError;
    }

}


