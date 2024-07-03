package msg.flight.manager.security;

import msg.flight.manager.persistence.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulesTasks {
    @Autowired
    private TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 10 * * ?")
    public  void manageDBTokens(){
        tokenRepository.manageTokens();
    }
}
