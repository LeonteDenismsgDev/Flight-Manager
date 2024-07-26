package msg.flight.manager.controller.airport;

import msg.flight.manager.persistence.dtos.airport.Airport;
import msg.flight.manager.services.airports.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("flymanager/airport")
public class AirportController {

    @Autowired
    AirportService service;

    @GetMapping("/list")
    public ResponseEntity<?> getAll(){
        return this.service.getAirports();
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@RequestParam(required = true) String icao){
        return this.service.getAirport(icao);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    public ResponseEntity<?> save(@RequestBody(required = true)Airport airport){
        return this.service.saveAirport(airport);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    public ResponseEntity<?> update(@RequestBody(required = true)Airport airport,@RequestParam(required = true) String icao){
        return this.service.updateAirport(icao,airport);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    public ResponseEntity<?> delete(@RequestParam(required = true)String icao){
        return this.service.deleteAirport(icao);
    }
}
