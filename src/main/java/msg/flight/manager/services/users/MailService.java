package msg.flight.manager.services.users;

import msg.flight.manager.persistence.dtos.flights.flights.FlightDescriptionDTO;
import msg.flight.manager.persistence.models.user.DBUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendUserCreatedMessage(DBUser user, String password) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getContactData().get("email"));
        mailMessage.setSubject("Hello to " + user.getCompany() + " airline");
        mailMessage.setText(message(user, password));
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    @Async
    public void sendDisableNotification(String email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Account disabling");
        mailMessage.setText("Your account has been disabled");
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    private String message(DBUser user, String password) {
        return "Hello " +
                user.getFirstName() +
                " !\n" +
                "We are happy to welcome you in our team." +
                "Hope you will have a great time with us." +
                "Your username and password is :" +
                user.getUsername() +
                ", " +
                password +
                ".\n Best regards!";
    }

}
