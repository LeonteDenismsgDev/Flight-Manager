package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.dtos.user.update.CanUpdateDTO;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository repository;
    private final List<String> registrationRoles = Arrays.asList(Role.CREW_ROLE.name(), Role.FLIGHT_MANAGER_ROLE.name());

    public boolean canRegisterUser(RegistrationUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (registrationRoles.contains(user.getRole())) {
            if (role.equals(Role.COMPANY_MANAGER_ROLE.name())) {
                SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                return securityUser.getCompany().equals(user.getCompany());
            }
            return role.equals(Role.ADMINISTRATOR_ROLE.name());

        } else {
            return role.equals(Role.ADMINISTRATOR_ROLE.name());
        }
    }

    public boolean canUpdatePassword(CanUpdateDTO dto) {
        return verifyUserCredentials(dto.getUsername());
    }

    public boolean canDisableUser(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        if (role.equals(Role.COMPANY_MANAGER_ROLE.name())) {
            Optional<SecurityUser> userToUpdate = repository.findByUsername(username);
            return userToUpdate.map(user -> user.getCompany().equals(securityUser.getCompany())).orElse(false);
        }
        return role.equals(Role.ADMINISTRATOR_ROLE.name());
    }

    public boolean canViewUserData(String username) {
        return verifyUserCredentials(username);
    }

    private boolean verifyUserCredentials(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        if (role.equals(Role.CREW_ROLE.name()) || role.equals(Role.FLIGHT_MANAGER_ROLE.name())) {
            return username.equals(securityUser.getUsername());
        }
        if (role.equals(Role.COMPANY_MANAGER_ROLE.name())) {
            Optional<SecurityUser> userToUpdate = repository.findByUsername(username);
            return userToUpdate.map(user -> user.getCompany().equals(securityUser.getCompany())).orElse(false);
        }
        return role.equals(Role.ADMINISTRATOR_ROLE.name());
    }
}
