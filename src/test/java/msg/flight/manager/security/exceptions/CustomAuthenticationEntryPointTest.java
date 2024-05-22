package msg.flight.manager.security.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class CustomAuthenticationEntryPointTest {
    @Test
    public void commence_sendsUnauthorized_whenForbiddenException() throws IOException, ServletException {
        CustomAuthenticationEntryPoint authenticationEntryPoint = new CustomAuthenticationEntryPoint();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthenticationException exception = Mockito.mock(AuthenticationException.class);
        authenticationEntryPoint.commence(request, response, exception);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthenticated!");
    }
}
