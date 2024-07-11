package msg.flight.manager.services.utils;

import lombok.experimental.UtilityClass;
import msg.flight.manager.security.SecurityUser;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserUtil {
    public SecurityUser getLoggedUser(){
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
