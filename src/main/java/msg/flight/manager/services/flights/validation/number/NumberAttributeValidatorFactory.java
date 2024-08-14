package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.number.validators.attributebased.IntegerAttributeValidator;
import msg.flight.manager.services.flights.validation.number.validators.attributebased.FloatAttributeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class NumberAttributeValidatorFactory {
    private static final FloatAttributeValidator floatAttributeValidator = new FloatAttributeValidator();
    private static final IntegerAttributeValidator integerAttributeValidator = new IntegerAttributeValidator();

    public AttributeValidator numberAttributeValidator(String attributeType) throws RuntimeException{
        if(attributeType.equals("number")){
            return integerAttributeValidator;
        }
        if(attributeType.equals("precision_number")){
            return floatAttributeValidator;
        }
       return  null;
    }
}
