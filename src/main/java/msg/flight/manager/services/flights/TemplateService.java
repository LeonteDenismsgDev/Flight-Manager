package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.dtos.flights.templates.MapTemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.UpdateTemplate;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.AttributesServiceUtils;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private SecurityUserUtil securityUser;


    public ResponseEntity<String> saveTemplate(RegisterTemplate templateDto) {
        SecurityUser loggedUser = securityUser.getLoggedUser();
        DBTemplate template = DBTemplate.builder().name(templateDto.getName())
                .createdBy(loggedUser.getUsername())
                .attributes(templateDto.getAttributes())
                .validations(templateDto.getValidations())
                .build();
        if (templateRepository.saveTemplate(template) != null) {
            return ResponseEntity.ok("saved");
        } else {
            return new ResponseEntity<>("Could not save the template with this name", HttpStatus.BAD_REQUEST);
        }
    }

    public TemplateTableResult viewTemplates(int page, int size) {
        return templateRepository.findTemplates(page, size).toTemplateTableResult(RegisterTemplate.class);
    }

    public ResponseEntity<String> deleteTemplate(String name) {
        long results = this.templateRepository.deleteTemplate(name);
        if (results > 0) {
            return new ResponseEntity<>("Template " + name + " deleted", HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>("Couldn't find template with name " + name, HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<String> updateTemplate(UpdateTemplate template) {
        DBTemplate dbTemplate = new DBTemplate(template.getNewName(), template.getCreatedBy(), template.getNewAttributes(), template.getNewValidations());
        long updated = this.templateRepository.updateTemplate(template.getOldName(), dbTemplate);
        if (updated == 0) {
            return new ResponseEntity<>("Template updated", HttpStatusCode.valueOf(200));
        } else if (updated == -1) {
            return new ResponseEntity<>("Could not find template", HttpStatusCode.valueOf(404));
        } else if (updated == -2) {
            return new ResponseEntity<>("Error while updating the template", HttpStatusCode.valueOf(400));
        } else {
            return new ResponseEntity<>("Error while updating the template, contact your admin", HttpStatusCode.valueOf(400));
        }
    }

    public ResponseEntity<MapTemplateDTO> getTemplate(String name) {
        TemplateDTO template = this.templateRepository.findTemplate(name);
        if (template == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        List<Map<String, Object>> validation = template.getValidations().stream().map(rule -> AttributesServiceUtils.stringJsonToMap(rule.getJson())).toList();
        MapTemplateDTO templateDTO = new MapTemplateDTO(template.getName(), template.getAttributes(), validation);
        return ResponseEntity.ok(templateDTO);
    }
}
