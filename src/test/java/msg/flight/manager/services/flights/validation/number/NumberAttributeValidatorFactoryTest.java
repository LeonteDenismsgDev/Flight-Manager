package msg.flight.manager.services.flights.validation.number;

import msg.flight.manager.services.flights.validation.template.number.NumberAttributeValidatorFactory;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.FloatAttributeValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.IntegerAttributeValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class NumberAttributeValidatorFactoryTest {
    private static final NumberAttributeValidatorFactory numberAttributeFactory = new NumberAttributeValidatorFactory();

    @Test
    public void  numberAttributeValidator_returnsIntegerValidator_whenNumberType(){
        Assertions.assertInstanceOf(IntegerAttributeValidator.class, numberAttributeFactory.numberAttributeValidator("number"));
    }

    @Test
    public void  numberAttributeValidator_returnsFloatValidator_whenPrecisionNumberType(){
        Assertions.assertInstanceOf(FloatAttributeValidator.class, numberAttributeFactory.numberAttributeValidator("precision_number"));
    }

    @Test
    public void  numberAttributeValidator_returnsNull_whenUnknownType(){
        Assertions.assertNull(numberAttributeFactory.numberAttributeValidator("unknown_type"));
    }
}
