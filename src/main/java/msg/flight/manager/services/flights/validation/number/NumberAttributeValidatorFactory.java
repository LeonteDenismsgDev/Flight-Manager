package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.number.validators.attributebased.IntegerAttributeValidator;
import msg.flight.manager.services.flights.validation.number.validators.attributebased.FloatAttributeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NumberAttributeValidatorFactory {
    @Autowired
    private FloatAttributeValidator floatAttributeValidator;
    @Autowired
    private IntegerAttributeValidator integerAttributeValidator;

    public AttributeValidator numberAttributeValidator(String type){
        if(type.contains("Float")){
            return floatAttributeValidator;
        }
        if(type.contains("Integer")){
            return integerAttributeValidator;
        }
        return null;
    }
}
