package msg.flight.manager.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flymanager/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody(required = true) RegistrationUser user) {
            return userService.save(user);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @GetMapping("/session/admin")
    public ResponseEntity<String> adminCheck(){
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('CREW_ROLE')")
    @GetMapping("/session/crew")
    public ResponseEntity<String> crewCheck(){
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('COMPANY_MANAGER_ROLE')")
    @GetMapping("/session/company_manager")
    public ResponseEntity<String> companyMangerCheck(){
        return ResponseEntity.ok("Confirmed");
    }

    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    @GetMapping("/session/flight_manager")
    public ResponseEntity<String> flightMangerCheck(){
        return ResponseEntity.ok("Confirmed");
    }

}
