package msg.flight.manager.services.flights.validation.date;

import msg.flight.manager.services.flights.validation.template.date.typebased.DateValidator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class DateValidatorTest {

    private static final DateValidator dateValidator= new DateValidator();

    @Test
    public void isBefore_returnsTrue_whenCalled(){
        Assertions.assertTrue(dateValidator.isBefore(LocalDateTime.now().minusDays(1),LocalDateTime.now()));
    }

    @Test
    public void isBefore_returnsFalse_whenCalled(){
        Assertions.assertFalse(dateValidator.isBefore(LocalDateTime.now(),LocalDateTime.now()));
    }

    @Test
    public void isAfter_returnsTrue_whenCalled(){
        Assertions.assertTrue(dateValidator.isAfter(LocalDateTime.now().plusDays(1),LocalDateTime.now()));
    }

    @Test
    public void isAfter_returnsFalse_whenCalled(){
        Assertions.assertFalse(dateValidator.isAfter(LocalDateTime.now(),LocalDateTime.now()));
    }

    @Test
    public void isInIntervalThrowsRuntimeException_whenInvalidStartAndEndDate(){
        Assertions.assertThrows(RuntimeException.class,()->dateValidator.isInInterval(LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now().minusDays(1)));
    }

    @Test
    public void isInIntervalReturnsTrue_whenCalled(){
        Assertions.assertTrue(dateValidator.isInInterval(LocalDateTime.now(),LocalDateTime.MIN,LocalDateTime.MAX));
    }

    @Test
    public void isInIntervalReturnsFalse_whenCalled(){
        Assertions.assertFalse(dateValidator.isInInterval(LocalDateTime.MIN,LocalDateTime.now(),LocalDateTime.MAX));
    }
}
