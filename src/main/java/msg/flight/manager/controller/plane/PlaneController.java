package msg.flight.manager.controller.plane;

import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.services.planes.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("flymanager/plane")
public class PlaneController {
    @Autowired
    private PlaneService service;

    @GetMapping("/view/all")
    public ResponseEntity<?> getAll(){
        return this.service.get();
    }

    @GetMapping("/view")
    public ResponseEntity<?> getOne(@RequestParam(required = true) String registrationNumber){
        return this.service.get(registrationNumber);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(required = true) String registrationNumber){
        return this.service.delete(registrationNumber);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Plane plane){
        return this.service.save(plane);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Plane plane, @RequestParam String registrationNumber){
        return this.service.update(registrationNumber,plane);
    }
}
