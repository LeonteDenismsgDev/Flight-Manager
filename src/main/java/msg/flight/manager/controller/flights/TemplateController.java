package msg.flight.manager.controller.flights;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.flights.templates.MapTemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.UpdateTemplate;
import msg.flight.manager.services.flights.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('FLIGHT_MANAGER_ROLE')")
@RequestMapping("flymanager/template")
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @PostMapping("/create")
    public ResponseEntity<String> registerTemplate(@Valid @RequestBody(required = true) RegisterTemplate template) {
        return templateService.saveTemplate(template);
    }

    @GetMapping("/view")
    public TemplateTableResult viewAllTemplates(@RequestParam(required = true) int page, @RequestParam(required = true) int size) {
        return templateService.viewTemplates(page, size);
    }
    @GetMapping(value = "/getTemplate")
    public ResponseEntity<MapTemplateDTO> getTemplate(@RequestParam(required = true)String name){
        return templateService.getTemplate(name);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTemplate(@Valid @RequestParam(required = true) String name){
        return templateService.deleteTemplate(name);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTemplate(@Valid @RequestBody(required = true)UpdateTemplate request){
        return templateService.updateTemplate(request);
    }
}
