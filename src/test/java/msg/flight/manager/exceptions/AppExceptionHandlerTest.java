package msg.flight.manager.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

public class AppExceptionHandlerTest {
    private final AppExceptionHandler exceptionHandler = new AppExceptionHandler();

    @Test
    public void handleExpirationTokenException_returnsUnauthorizedResponse_whenJWTTokenExpired() {
        ResponseEntity<Void> response = exceptionHandler.handleExpirationTokenException(new ExpiredJwtException(null, null, "Token is expired"));
        ResponseEntity<Void> expected = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        Assertions.assertEquals(expected, response);

    }

    @Test
    public void handleRuntimesExceptions_returnsBadRequestResponse_whenRuntimeException() {
        ResponseEntity<String> response = exceptionHandler.handleRuntimesExceptions(new RuntimeException("runtimeException"));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("runtimeException");
        Assertions.assertEquals(expected, response);
    }

    @Test
    public void handleBadCredentials_returnsUnauthorizedResponse_whenBadCredentialsException() {
        ResponseEntity<String> response = exceptionHandler.handleBadCredentials(new BadCredentialsException("bad credentials"));
        ResponseEntity<String> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials!");
        Assertions.assertEquals(expected, response);
    }

    @Test
    public void handleValidationExceptions_returnsBadRequestResponse_whenInvalidData() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult result = Mockito.mock(BindingResult.class);
        Mockito.when(result.getFieldErrors()).thenReturn(List.of(new FieldError("attribute", "attribute", "error message")));
        Mockito.when(exception.getBindingResult()).thenReturn(result);
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(exception);
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("attribute", "error message"));
        Assertions.assertEquals(expected, response);
    }

    @Test
    public void handleAccessDenied_returnsForbiddenResponse_whenNotEnaughAuthorities() throws Exception {
        ResponseEntity<String> response = exceptionHandler.handleAccessDenied(new AccessDeniedException("No authorities"));
        ResponseEntity<String> expected = new ResponseEntity<>("No authorities", HttpStatus.FORBIDDEN);
        Assertions.assertEquals(expected, response);
    }

}
