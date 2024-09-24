package msg.flight.manager.services.flights;

import lombok.extern.slf4j.Slf4j;
import msg.flight.manager.persistence.models.flights.DBFlight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlightsMailService {
    public static final String NOTIFICATION_RECORDS_ERROR = "Notification records couldn't be saved for : ";
    private final JavaMailSender javaMailSender;

    @Autowired
    FlightsMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendWarningNotification(String email, List<DBFlight> flights) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String mailTextMessage = buildDisableWarningMessage(flights);
        mailMessage.setTo(email);
        mailMessage.setSubject("Upcoming flights will be disabled");
        mailMessage.setText(mailTextMessage);
        try {
            javaMailSender.send(mailMessage);
            return mailTextMessage;
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    private String buildDisableWarningMessage(List<DBFlight> flightDescriptions) {
        StringBuilder mailBuilder = new StringBuilder();
        mailBuilder.append("The next  upcoming flights will be disabled:");
        for (DBFlight flightDescription : flightDescriptions) {
            mailBuilder.append("\n")
                    .append(flightDescription.getDestination().getAirportName())
                    .append("-")
                    .append(flightDescription.getDeparture().getAirportName())
                    .append(" from ")
                    .append(flightDescription.getDepartureTime());
        }
        mailBuilder.append("Please assign a crew and a plane for the flight");
        return mailBuilder.toString();
    }

    public String sendDisabledNotification(String email, List<String> flightsId) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String mailTextMessage = buildDisableMessage(flightsId);
        mailMessage.setTo(email);
        mailMessage.setSubject("Upcoming flights will be disabled");
        mailMessage.setText(mailTextMessage);
        try {
            javaMailSender.send(mailMessage);
            return mailTextMessage;
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    private String buildDisableMessage(List<String> flightsId) {
        StringBuilder mailBuilder = new StringBuilder();
        mailBuilder.append("The flight with the id :");
        for (String id : flightsId) {
            mailBuilder.append(id)
                    .append(", ");
        }
        mailBuilder.append(" have been disabled");
        return mailBuilder.toString();
    }
}
