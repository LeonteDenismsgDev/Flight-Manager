package msg.flight.manager.services;

import jakarta.mail.MessagingException;
import msg.flight.manager.persistence.dtos.user.RegistrationUser;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.services.utils.UserServicesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    MailService mailService;

    @Transactional
    public ResponseEntity<String> save(RegistrationUser registrationUser) throws MessagingException {
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
                .build();
        DBUser savedUser = userRepository.save(dbUser);
        mailService.sendUserCreatedMessage(savedUser, password);
        return ResponseEntity.ok(savedUser.getUsername());
    }


}
