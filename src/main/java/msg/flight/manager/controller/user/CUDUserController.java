package msg.flight.manager.controller.user;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.CrewUpdateUser;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.dtos.user.update.UpdatePassword;
import msg.flight.manager.services.users.UserService;
import msg.flight.manager.services.annotations.CanChangePassword;
import msg.flight.manager.services.annotations.CanDisableUser;
import msg.flight.manager.services.annotations.CanRegisterUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flymanager/user")
public class CUDUserController {

    @Autowired
    UserService userService;

    @CanRegisterUser
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody(required = true) RegistrationUser user) {
        return userService.save(user);
    }

    @CanChangePassword
    @PutMapping("/crew/update")
    public ResponseEntity<String> updateUserData(@Valid @RequestBody(required = true) CrewUpdateUser dto) {
        return userService.updateUser(dto);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @PutMapping("/admin/update")
    public ResponseEntity<String> updateUser(@Valid @RequestBody(required = true) AdminUpdateUser user) {
        return userService.updateUser(user);
    }

    @CanChangePassword
    @PutMapping("password/update")
    public ResponseEntity<String> updatePassword(@RequestBody(required = true) UpdatePassword dto) {
        return userService.updatePassword(dto);
    }

    @CanDisableUser
    @PutMapping("enable/update")
    public ResponseEntity<String> toggleEnable(@RequestParam(required = true) String username) {
        return userService.toggleEnable(username);
    }

}
