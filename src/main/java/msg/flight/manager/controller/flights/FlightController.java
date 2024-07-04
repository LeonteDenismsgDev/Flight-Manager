package msg.flight.manager.controller.flights;

import msg.flight.manager.services.flights.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("flymanager/flights")
public class FlightController {
    @Autowired
    private FlightService flightService;

}
