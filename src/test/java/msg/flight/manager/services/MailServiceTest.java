package msg.flight.manager.services;

import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {


    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailService mailService;
    @Captor
    ArgumentCaptor<SimpleMailMessage> mailCaptor;


    @Test
    void sendUserCreatedMessage_sendExpectedMessage_whenCalled() throws Exception {
        mailService.sendUserCreatedMessage(createDBUser(), "password");
        String expectedMessage = createMessage();
        verify(javaMailSender, times(1)).send(mailCaptor.capture());
        Assertions.assertEquals(expectedMessage, mailCaptor.getValue().getText());
    }

    @Test
    void sendUserCreatedMessage_ThrowsExceptionMessage_whenCalled() throws Exception {
        Mockito.doThrow(new MailException("Simulated mail server failure") {
        }).when(javaMailSender).send(createSimpleMailRegisterMessage());
        assertThrows(RuntimeException.class, () -> {
            mailService.sendUserCreatedMessage(createDBUser(), "password");
        });
    }

    @Test
    void sendDisableNotification_ThrowsExceptionMessage_whenCalled() throws Exception {
        Mockito.doThrow(new MailException("Simulated mail server failure") {
        }).when(javaMailSender).send(createSimpleMailDisableMessage());
        assertThrows(RuntimeException.class, () -> {
            mailService.sendDisableNotification("email");
        });
    }

    private DBUser createDBUser() {
        return DBUser.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactData(Map.of("email", "email"))
                .address("address")
                .company("company")
                .username("username")
                .password("password")
                .enabled(true)
                .role(Role.CREW_ROLE.name())
                .build();
    }

    private SimpleMailMessage createSimpleMailRegisterMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("email");
        mailMessage.setSubject("Hello to " + "company" + " airline");
        mailMessage.setText(createMessage());
        return mailMessage;
    }

    private SimpleMailMessage createSimpleMailDisableMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("email");
        mailMessage.setSubject("Account disabling");
        mailMessage.setText("Your account has been disabled");
        return mailMessage;
    }


    private static String createMessage() {
        return "Hello firstName !\n" +
                "We are happy to welcome you in our team." +
                "Hope you will have a great time with us." +
                "Your username and password is :username, password.\n Best regards!";
    }
}

