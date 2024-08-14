package msg.flight.manager.controller.flights;

import com.fasterxml.jackson.databind.JsonNode;
import msg.flight.manager.services.flights.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("flymanager/flight")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> saveFlight(@RequestBody(required = true)JsonNode flight, @RequestParam(required = true) String templateName){
        return flightService.saveFlight(flight,templateName);
    }

    @GetMapping("/view")
    public List<Map<String,Object>> getFlights(@RequestParam(required = true)Integer page, @RequestParam(required = true)Integer size){
        return flightService.getFlights(page,size);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> deleteFlight(@RequestParam(required = true)String flightId){
        return flightService.deleteFlight(flightId);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> updateFlight(@RequestBody(required = true) JsonNode flight,@RequestParam(required = true) String flightId){
        return flightService.updateFlight(flight,flightId);
    }
}
