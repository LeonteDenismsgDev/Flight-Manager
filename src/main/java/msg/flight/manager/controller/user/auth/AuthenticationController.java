package msg.flight.manager.controller.user.auth;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationRequest;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationResponse;
import msg.flight.manager.services.users.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @GetMapping("/session/admin")
    public ResponseEntity<String> adminCheck() {
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('CREW_ROLE')")
    @GetMapping("/session/crew")
    public ResponseEntity<String> crewCheck() {
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('COMPANY_MANAGER_ROLE')")
    @GetMapping("/session/company_manager")
    public ResponseEntity<String> companyMangerCheck() {
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    @GetMapping("/session/flight_manager")
    public ResponseEntity<String> flightMangerCheck() {
        return ResponseEntity.ok("Confirmed");
    }
}
