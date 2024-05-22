package msg.flight.manager.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

public class AppExceptionHandlerTest {
    private final AppExceptionHandler exceptionHandler = new AppExceptionHandler();

    @Test
    public void handleRuntimesExceptions_returnsBadRequestResponse_whenRuntimeException(){
        ResponseEntity<String> response = exceptionHandler.handleRuntimesExceptions(new RuntimeException("runtimeException"));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("runtimeException");
        Assertions.assertEquals(expected,response);
    }

    @Test
    public void handleBadCredentials_returnsUnauthorizedResponse_whenBadCredentialsException(){
        ResponseEntity<String> response = exceptionHandler.handleBadCredentials(new BadCredentialsException("bad credentials"));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials!");
        Assertions.assertEquals(expected,response);
    }

    @Test
    public void handleValidationExceptions_returnsBadRequestResponse_whenInvalidData(){
        MethodArgumentNotValidException exception =Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.getFieldErrors()).thenReturn(List.of(new FieldError("attribute", "attribute", "error message")));
        Mockito.when(exception.getBindingResult()).thenReturn(result);
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("attribute","error message"));
        Assertions.assertEquals(expected,response);
    }

}
