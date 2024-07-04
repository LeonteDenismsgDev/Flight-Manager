package msg.flight.manager.services.flights.validation.text;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.text.validators.attributebased.StringAttributeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StringAttributeValidatorFactory {
    @Autowired
    private StringAttributeValidator attributeValidator;

    public AttributeValidator stringAttributeValidator(String type) {
        if(type.contains("String")){
            return attributeValidator;
        }
        return  null;
    }
}
