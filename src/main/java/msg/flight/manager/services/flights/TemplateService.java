package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
}
