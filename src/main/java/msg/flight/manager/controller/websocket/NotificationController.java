package msg.flight.manager.controller.websocket;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalTime;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("flymanager")
public class NotificationController {

    int counter = 0;

    @GetMapping(value = "/notification", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "SSE MVC - " + LocalTime.now().toString())
                .take(1);
    }
}
