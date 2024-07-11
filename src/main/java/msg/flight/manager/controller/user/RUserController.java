package msg.flight.manager.controller.user;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.update.UserTableResult;
import msg.flight.manager.services.users.UserService;
import msg.flight.manager.services.annotations.CanViewUserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("flymanager/view")
public class RUserController {
    @Autowired
    UserService userService;

    @CanViewUserData
    @GetMapping("/user")
    public ResponseEntity<AdminUpdateUser> getUserData(@RequestParam(required = true) String username) {
        return userService.viewUserData(username);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE') or hasAuthority('COMPANY_MANAGER_ROLE')")
    @PostMapping("/users")
    public UserTableResult findUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestBody(required = true) @Valid UsersFilterOptions filters) {
        return userService.findUsers(filters, page, size);
    }


    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    @GetMapping("/availableUser")
    public List<String> findAvailableCrew(@RequestParam(required = true) LocalDateTime startTime, @RequestParam(required = true) LocalDateTime endTime, @RequestParam(required = true) String startLocation) {
        return userService.findAvailableUsers(startTime, endTime, startLocation);
    }
}
