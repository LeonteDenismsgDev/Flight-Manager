package msg.flight.manager.services.flights.validation.template.number;

import msg.flight.manager.services.flights.validation.template.AttributeValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.IntegerAttributeValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.FloatAttributeValidator;

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
