package msg.flight.manager.controller.flights;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.dtos.flights.attributes.RegisterAttribute;
import msg.flight.manager.services.flights.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
@RequestMapping("flymanager/attribute")
public class AttributeController {
    @Autowired
    private AttributeService attributeService;

    @PostMapping("/save")
    public ResponseEntity<String> registerAttribute(@Valid @RequestBody RegisterAttribute attribute) {
        return attributeService.createAttribute(attribute);
    }

    @GetMapping("/view")
    public List<AttributeDTO> getAttributes() {
        return attributeService.getAppAttributes();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAttribute(@Valid @RequestParam String id){
        return attributeService.deleteAttribute(id);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateAttribute(@Valid @RequestParam String id, @Valid @RequestBody(required = true) AttributeDTO updatedAttribute){
        return attributeService.updateAttribute(id,updatedAttribute);
    }
}
