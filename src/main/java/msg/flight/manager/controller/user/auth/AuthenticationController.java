package msg.flight.manager.controller.user.auth;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationRequest;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationResponse;
import msg.flight.manager.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flymanager/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody(required = true) AuthenticationRequest request) {
        return authenticationService.login(request);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        return authenticationService.logout(token);
    }
}
