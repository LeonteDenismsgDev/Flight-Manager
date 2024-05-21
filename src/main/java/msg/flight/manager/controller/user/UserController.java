package msg.flight.manager.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("flymanager/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationUser user) {
        try {
            return userService.save(user);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
