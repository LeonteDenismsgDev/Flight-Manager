package msg.flight.manager.services.utils;

import msg.flight.manager.security.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUserUtil {
    public SecurityUser getLoggedUser(){
        return (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
