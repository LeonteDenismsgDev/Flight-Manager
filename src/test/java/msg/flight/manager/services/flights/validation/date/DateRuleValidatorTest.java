package msg.flight.manager.services.flights.validation.date;

import msg.flight.manager.services.flights.validation.template.date.typebased.DateRuleValidator;
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
public class DateRuleValidatorTest {

    @Test
    public void validate_returnsExpectedResult_whenRulesAreNull() {
        DateRuleValidator dateValidator = new DateRuleValidator();
        String expected = "";
        Assertions.assertEquals(expected, dateValidator.validate(LocalDateTime.now(), "testAttribute"));
    }

    @Test
    public void validate_returnsExpectedResult_whenAllValidationRulesAreBroken() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateRuleValidator dateValidator = new DateRuleValidator(dateTime, LocalDateTime.MAX, dateTime, LocalDateTime.MAX, new DateValidator());
        String expected = "testAttribute should be before " + dateTime + "\n" +
                "testAttribute should be after " + LocalDateTime.MAX + "\n" +
                "testAttribute should be between " + dateTime + " - " + LocalDateTime.MAX + "\n";
        Assertions.assertEquals(expected,dateValidator.validate(dateTime,"testAttribute"));
    }

    @Test
    public void validate_returnsExpectedResult_whenDateValidatesDateRules(){
        String expected = "";
        LocalDateTime dateTime = LocalDateTime.now();
        DateRuleValidator dateRuleValidator = new DateRuleValidator(dateTime.plusDays(2),LocalDateTime.MIN,LocalDateTime.MIN,LocalDateTime.MAX,new DateValidator());
        Assertions.assertEquals(expected,dateRuleValidator.validate(dateTime,"textAttribute"));
    }

    @Test
    public void validate_returnsExpectedResult_whenNullDateStartRules(){
        String expected = "";
        LocalDateTime dateTime = LocalDateTime.now();
        DateRuleValidator dateRuleValidator = new DateRuleValidator(dateTime.plusDays(2),LocalDateTime.MIN,null,LocalDateTime.MAX,new DateValidator());
        Assertions.assertEquals(expected,dateRuleValidator.validate(dateTime,"textAttribute"));
    }

    @Test
    public void validate_returnsExpectedResult_whenNullDateEndRules(){
        String expected = "";
        LocalDateTime dateTime = LocalDateTime.now();
        DateRuleValidator dateRuleValidator = new DateRuleValidator(dateTime.plusDays(2),LocalDateTime.MIN,LocalDateTime.MIN,null,new DateValidator());
        Assertions.assertEquals(expected,dateRuleValidator.validate(dateTime,"textAttribute"));
    }
}
