package msg.flight.manager.services.flights.validation;

import msg.flight.manager.services.flights.validation.template.AttributeValidatorFactory;
import msg.flight.manager.services.flights.validation.template.array.attributebased.ArrayAttributeValidator;
import msg.flight.manager.services.flights.validation.template.date.attributebased.DateAttributeValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.FloatAttributeValidator;
import msg.flight.manager.services.flights.validation.template.number.validators.attributebased.IntegerAttributeValidator;
import msg.flight.manager.services.flights.validation.template.text.attributebased.StringAttributeValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class AttributeValidatorFactoryTest {

    private final AttributeValidatorFactory validatorsFactory = new AttributeValidatorFactory();

    @Test
    public void createValidator_returnsWhenStringAttributeValidatorInstance_whenTextAttributeType(){
        Assertions.assertInstanceOf(StringAttributeValidator.class, validatorsFactory.createValidator("text"));
    }

    @Test
    public void createValidator_returnsFloatAttributeValidatorInstance_whenPrecisionNumberAttributeType(){
        Assertions.assertInstanceOf(FloatAttributeValidator.class, validatorsFactory.createValidator("precision_number"));
    }

    @Test
    public void createValidator_returnsIntAttributeValidatorInstance_whenNumberAttributeType(){
        Assertions.assertInstanceOf(IntegerAttributeValidator.class,validatorsFactory.createValidator("number"));
    }

    @Test
    public void createValidator_returnsDateAttributeValidatorInstance_whenDateAttributeType(){
        Assertions.assertInstanceOf(DateAttributeValidator.class,validatorsFactory.createValidator("date"));
    }

    @Test
    public void createValidator_returnsArrayAttributeValidatorInstance_whenArrayType(){
        Assertions.assertInstanceOf(ArrayAttributeValidator.class,validatorsFactory.createValidator("array"));
    }

    @Test
    public void createValidator_throwsRuntimeException_whenInvalidType(){
        Assertions.assertThrows(RuntimeException.class,()->validatorsFactory.createValidator("invalidType"));
    }
}
