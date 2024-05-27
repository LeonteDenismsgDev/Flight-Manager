package msg.flight.manager.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulesTasks {
    @Scheduled(cron = "0 0 10 * * ?")
    public  void manageDBTokens(){

    }
}
