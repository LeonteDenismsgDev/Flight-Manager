package msg.flight.manager.scheduling;

import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.services.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class FlightScheduler {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CompanyRepository companyRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void executeAnnulmentTask() {
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalDateTime.MIN);
        LocalDateTime monthForward = todayEnd.plusMonths(1);
        List<DBCompany> companies = companyRepository.getAll();
        for(DBCompany company : companies){
            notificationService.sendNotification(company.getName(),todayEnd,monthForward);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateHistory() {
        LocalDateTime historyTime = LocalDateTime.now().minusDays(1);
    }

}
