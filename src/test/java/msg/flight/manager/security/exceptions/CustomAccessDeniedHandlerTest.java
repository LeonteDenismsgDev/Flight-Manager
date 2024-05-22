package msg.flight.manager.security.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class CustomAccessDeniedHandlerTest {
    @Test
    public void handle_sendsForbiddenResponse_whenForbiddenException() throws IOException, ServletException {
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");
        accessDeniedHandler.handle(request,
                response,
                accessDeniedException);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
}
