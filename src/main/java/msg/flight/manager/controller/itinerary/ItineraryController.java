package msg.flight.manager.controller.itinerary;

import msg.flight.manager.persistence.dtos.itinerary.GetItineraries;
import msg.flight.manager.persistence.dtos.itinerary.Itinerary;
import msg.flight.manager.services.itineraries.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("flymanager/itinerary")
public class ItineraryController {
    @Autowired
    ItineraryService service;

    @GetMapping("/view/all")
    public ResponseEntity<?> getAll(){
        return this.service.get();
    }

    @GetMapping("/view")
    public ResponseEntity<?> get(@RequestParam (required = true) String id){
        return this.service.get(id);
    }

    @PostMapping("/view/filtered")
    public ResponseEntity<?> filter(@RequestBody (required = true)GetItineraries request){
        return this.service.getFiltered(request);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody(required = true)Itinerary itinerary){
        return this.service.save(itinerary);
    }

//    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody(required = true)Itinerary itinerary,@RequestParam(required = true)String id){
        return this.service.update(id, itinerary);
    }
}
