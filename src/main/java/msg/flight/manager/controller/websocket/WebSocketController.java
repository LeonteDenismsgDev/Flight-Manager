package msg.flight.manager.controller.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public String send(String msg){
        return "Echo: " + msg;
    }
}
