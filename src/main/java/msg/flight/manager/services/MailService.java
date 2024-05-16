package msg.flight.manager.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import msg.flight.manager.persistence.models.user.DBUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String IMAGE_PATH = "";

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendUserCreatedMessage(DBUser user, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(user.getContactData().get("email"));
        helper.setText(message(user, password));
        helper.setSubject("Hello  to " + user.getCompany() + " airline");
        //ClassPathResource imageResource = new ClassPathResource(IMAGE_PATH);
        //helper.addInline("image", imageResource);
        javaMailSender.send(mimeMessage);
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
