package msg.flight.manager.services.flights.validation.date;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.date.validators.attributebased.DateAttributeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DateAttributeValidatorFactory {
    @Autowired
    private DateAttributeValidator attributeValidator;

    public AttributeValidator dateTimeAttributeValidator(String type){
        if(type.contains("LocalDateTime")){
            return attributeValidator;
        }
        return null;
    }
}
