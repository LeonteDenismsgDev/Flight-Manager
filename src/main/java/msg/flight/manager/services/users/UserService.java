package msg.flight.manager.services.users;

import lombok.SneakyThrows;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.auth.AuthenticationResponse;
import msg.flight.manager.persistence.dtos.user.update.*;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.TokenRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.persistence.repositories.WorkHoursRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import msg.flight.manager.services.utils.UserServicesUtil;
import org.slf4j.event.KeyValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private WorkHoursRepository workHoursRepository;
    @Autowired
    private SecurityUserUtil securityUser = new SecurityUserUtil();

    @Transactional
    public ResponseEntity<String> save(RegistrationUser registrationUser) {
        String password = UserServicesUtil.generatePassword();
        DBUser dbUser = DBUser.builder()
                .firstName(registrationUser.getFirstName())
                .lastName(registrationUser.getLastName())
                .contactData(registrationUser.getContactData())
                .address(registrationUser.getAddress())
                .company(registrationUser.getCompany())
                .username(UserServicesUtil.generateUsername(registrationUser))
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .role(registrationUser.getRole())
                .canBeViewBy(UserServicesUtil.getAccessRoles(registrationUser.getRole()))
                .build();
        DBUser savedUser = userRepository.save(dbUser);
        mailService.sendUserCreatedMessage(savedUser, password);
        return ResponseEntity.ok(savedUser.getUsername());
    }

    public ResponseEntity<?> getCurrent(){
        try {
            SecurityUser user = securityUser.getLoggedUser();
            return new ResponseEntity<>(new AuthenticationResponse("",user.getUsername(),user.getRole()), HttpStatusCode.valueOf(202));
        } catch(Exception e){
            return new ResponseEntity<>(new AuthenticationResponse("","",""),HttpStatusCode.valueOf(202));
        }
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<String> updateUser(UpdateUserDto userDto) {
        long updates = userRepository.updateUser(userDto);
        if (updates > 0) {
            return ResponseEntity.ok("Updated user");
        }
        return new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<String> updatePassword(UpdatePassword updatePassword) {
        String encodedPassword = passwordEncoder.encode(updatePassword.getPassword());
        long updates = userRepository.updatePassword(encodedPassword, updatePassword.getUsername());
        if (updates > 0) {
            return ResponseEntity.ok("changed password");
        }
        return new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<String> toggleEnable(String username) {
        KeyValuePair pair = userRepository.toggleEnable(username);
        if (pair == null) {
            return new ResponseEntity<>("username not found", HttpStatus.BAD_REQUEST);
        }
        if(!(boolean)pair.value) {
            tokenRepository.disableUser(username);
            mailService.sendDisableNotification(pair.key);
            return ResponseEntity.ok("The account has been disabled");
        }else{
            return ResponseEntity.ok("The account is enable again");
        }
    }

    public ResponseEntity<AdminUpdateUser> viewUserData(String username) {
        AdminUpdateUser user = userRepository.findDataByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public UserTableResult findUsers(UsersFilterOptions filters, int page, int size) {
        SecurityUser user = securityUser.getLoggedUser();
        Class<? extends UpdateUserDto> printClass = CrewUpdateUser.class;
        if (user.getRole().equals(Role.ADMINISTRATOR_ROLE.name())) {
            printClass = AdminUpdateUser.class;
            filters.setCompany(".*" + filters.getCompany() + ".*");
        } else {
            filters.setCompany("^" + user.getCompany() + "$");
        }
        return userRepository.filterUsers(PageRequest.of(page, size), filters, user.getRole(), user.getCompany()).toUserTableResult(printClass);
    }

    public List<String> findAvailableUsers(LocalDateTime startTime, LocalDateTime endTime, String startLocation) {
        return workHoursRepository.findAvailableUsers(startTime, endTime, startLocation);
    }
}
