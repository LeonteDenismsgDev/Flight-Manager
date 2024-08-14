package msg.flight.manager.services.flights.validation;

import msg.flight.manager.services.flights.validation.array.attributebased.ArrayAttributeValidator;
import msg.flight.manager.services.flights.validation.date.attributebased.DateAttributeValidator;
import msg.flight.manager.services.flights.validation.number.NumberAttributeValidatorFactory;
import msg.flight.manager.services.flights.validation.text.attributebased.StringAttributeValidator;


public class AttributeValidatorFactory {
    private static final StringAttributeValidator textAttributeValidator = new StringAttributeValidator();
    private static final NumberAttributeValidatorFactory numberValidationFactory = new NumberAttributeValidatorFactory();
    private static final DateAttributeValidator dateAttributeValidator = new DateAttributeValidator();
    private static final ArrayAttributeValidator arrayAttributeValidator = new ArrayAttributeValidator();

    public AttributeValidator createValidator(String attributeType) throws RuntimeException{
        if(attributeType.equals("text")) {
            return textAttributeValidator;
        }else{
            AttributeValidator numberValidator = numberValidationFactory.numberAttributeValidator(attributeType);
            if(numberValidator != null){
                return numberValidator;
            }else{
                if(attributeType.equals("date")){
                    return dateAttributeValidator;
                }else{
                    if(attributeType.equals("array")){
                        return arrayAttributeValidator;
                    }
                }
            }
        }
        throw new RuntimeException("Invalid Validation Type!");
    }
}
