package msg.flight.manager.controller.flights;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.flights.flights.*;
import msg.flight.manager.services.flights.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("flymanager/flight")
public class FlightController {
    @Autowired
    private FlightService flightService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> saveFlight(@Valid @RequestBody(required = true) RegisterFlightDTO flightDTO) {
        return flightService.saveFlight(flightDTO);
    }

    @PostMapping("/view")
    public List<FlightDescriptionDTO> getFlights(@RequestBody(required = true) GetDateFlightsDTO getDateFlightsDTO) {
        return flightService.getDateFlights(getDateFlightsDTO);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> deleteFlight(@RequestParam(required = true) String flightId) {
        return flightService.deleteFlight(flightId);
    }

    @DeleteMapping("/recurrence/delete")
    @PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
    public ResponseEntity<String> deleteRecurrence(String id) {
        return flightService.deleteRecurrence(id);
    }

    @PostMapping("/update/flight")
    public ResponseEntity<String> updateFlight(@RequestBody(required = true) UpdateFlightDTO flight,
                                               @RequestParam(required = true) String flightId) {
        return flightService.updateFlight(flight,flightId);
    }

    @PostMapping("/update/recurrence/flight")
    public ResponseEntity<String> updateRecurrenceFlight(@RequestBody(required = true) RegisterRecurrenceFlight flight) {
        return flightService.updateNonExistentRecurrenceFlight(flight);
    }

    @PostMapping("/history")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE') or hasAuthority('COMPANY_MANAGER_ROLE') or hasAuthority('FLIGHT_MANAGER_ROLE')")
    public List<FlightDescriptionDTO> flightsHistory(@RequestParam(required = false) Integer months){
        return  new ArrayList<>();
    }
}
