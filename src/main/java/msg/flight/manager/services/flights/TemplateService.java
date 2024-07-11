package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.dtos.flights.templates.UpdateTemplate;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepository;


    public ResponseEntity<String> saveTemplate(RegisterTemplate templateDto) {
        SecurityUser loggedUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DBTemplate template = DBTemplate.builder().name(templateDto.getName())
                .createdBy(loggedUser.getUsername())
                .attributes(templateDto.getAttributes())
                .validations(templateDto.getValidations())
                .build();
        if (templateRepository.saveTemplate(template) != null) {
            return ResponseEntity.ok("saved");
        } else {
            return new ResponseEntity<>("There is already a template with this name", HttpStatus.BAD_REQUEST);
        }
    }

    public TemplateTableResult viewTemplates(int page, int size) {
        return templateRepository.findTemplates(page, size).toTemplateTableResult(RegisterTemplate.class);
    }

    public ResponseEntity<String> deleteTemplate(String name){
        long results = this.templateRepository.deleteTemplate(name);
        if(results > 0){
            return new ResponseEntity<String>("Template "+name+" deleted",HttpStatusCode.valueOf(200));
        }
        else{
            return new ResponseEntity<String>("Couldn't find template with name " + name, HttpStatusCode.valueOf(404));
        }
    }

    public ResponseEntity<String> updateTemplate(UpdateTemplate template){
        DBTemplate dbTemplate = new DBTemplate(template.getNewName(), template.getCreatedBy(),template.getNewAttributes(),template.getNewValidations());
        try {
            long updated = this.templateRepository.updateTemplate(template.getOldName(), dbTemplate);
            if (updated == 0) {
                return new ResponseEntity<>("Template updated", HttpStatusCode.valueOf(200));
            } else if(updated == -1) {
                return new ResponseEntity<>("Could not find template", HttpStatusCode.valueOf(404));
            } else if (updated == -2) {
                return new ResponseEntity<>("Error while updating the template", HttpStatusCode.valueOf(400));
            }else{
                return new ResponseEntity<>("Error while updating the template, contact your admin", HttpStatusCode.valueOf(400));
            }
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(400));
        }
    }

}
