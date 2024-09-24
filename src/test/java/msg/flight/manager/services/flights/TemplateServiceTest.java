package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.dtos.flights.templates.MapTemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.UpdateTemplate;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.bson.Document;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceTest {
    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private SecurityUserUtil securityUserUtil;
    @InjectMocks
    private TemplateService templateService;

    @Test
    public void saveTemplate_returnsSavedResponse_whenTemplateSavedSuccessfully() {
        DBTemplate dbTemplate = createDBTemplate();
        ResponseEntity<String> expected = ResponseEntity.ok("saved");
        Mockito.when(securityUserUtil.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(templateRepository.saveTemplate(dbTemplate)).thenReturn(dbTemplate);
        Assertions.assertEquals(expected, templateService.saveTemplate(createRegisterTemplate()));
    }

    @Test
    public void saveTemplate_returnsSavedFailedResponse_whenRepositoryFailsSavingTheTemplate() {
        ResponseEntity<String> expected = new ResponseEntity<>("Could not save the template with this name", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUserUtil.getLoggedUser()).thenReturn(createSecurityUser());
        Assertions.assertEquals(expected, templateService.saveTemplate(createRegisterTemplate()));
    }

    @Test
    public void viewTemplates_returnsExpectedResponse_whenCalled() {
        TemplateTableResult expected = createTemplateTableResult();
        Mockito.when(templateRepository.findTemplates(0, 2)).thenReturn(createTemplatesTableResult());
        Assertions.assertEquals(expected, templateService.viewTemplates(0, 2));
    }

    @Test
    public void deleteFlight_returnsTemplateDeletedResponse_whenTemplateDeletedSuccessfully() {
        ResponseEntity<String> expected = new ResponseEntity<String>("Template " + "name" + " deleted", HttpStatusCode.valueOf(200));
        Mockito.when(templateRepository.deleteTemplate("name")).thenReturn(1L);
        Assertions.assertEquals(expected, templateService.deleteTemplate("name"));
    }

    @Test
    public void deleteFlight_returnsFailedDeletedTemplateResponse_whenTemplateRepositoryFailsDeletion() {
        ResponseEntity<String> expected = new ResponseEntity<>("Couldn't find template with name " + "name", HttpStatusCode.valueOf(404));
        Mockito.when(templateRepository.deleteTemplate("name")).thenReturn(0L);
        Assertions.assertEquals(expected, templateService.deleteTemplate("name"));
    }

    @Test
    public void updateTemplate_returnsUpdatedResponse_whenTemplateUpdatedSuccessfully() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Template updated", HttpStatusCode.valueOf(200));
        Mockito.when(templateRepository.updateTemplate("name", createDBTemplate())).thenReturn(0);
        Assertions.assertEquals(expected, templateService.updateTemplate(createUpdateTemplate()));
    }

    @Test
    public void updateTemplate_returnsTemplateNotFoundResponse_whenRepositoryDoesNotFindTemplate() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Could not find template", HttpStatusCode.valueOf(404));
        Mockito.when(templateRepository.updateTemplate("name", createDBTemplate())).thenReturn(-1);
        Assertions.assertEquals(expected, templateService.updateTemplate(createUpdateTemplate()));
    }

    @Test
    public void updateTemplate_returnsFailedUpdate_whenRepositoryFailsTemplateUpdate() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Error while updating the template", HttpStatusCode.valueOf(400));
        Mockito.when(templateRepository.updateTemplate("name", createDBTemplate())).thenReturn(-2);
        Assertions.assertEquals(expected, templateService.updateTemplate(createUpdateTemplate()));
    }

    @Test
    public void updateTemplate_returnsFailedUpdate_whenRepositoryFailsUpdate() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Error while updating the template, contact your admin", HttpStatusCode.valueOf(400));
        Mockito.when(templateRepository.updateTemplate("name", createDBTemplate())).thenReturn(1);
        Assertions.assertEquals(expected, templateService.updateTemplate(createUpdateTemplate()));
    }

    @Test
    public void getTemplate_returnsExpectedResponse_whenExistentTemplate() {
        ResponseEntity<MapTemplateDTO> expected = ResponseEntity.ok(createMapTemplateDTO());
        Mockito.when(templateRepository.findTemplate("name")).thenReturn(createTemplateDTO());
        Assertions.assertEquals(expected, templateService.getTemplate("name"));
    }

    @Test
    public void getTemplate_returnsNotFoundResponse_whenNonexistentTemplate() {
        ResponseEntity<MapTemplateDTO> expected = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        Assertions.assertEquals(expected, templateService.getTemplate("name"));
    }

    public TemplateDTO createTemplateDTO() {
        return TemplateDTO.builder()
                .name("name")
                .attributes(new HashSet<>())
                .validations(new ArrayList<>())
                .build();
    }

    public MapTemplateDTO createMapTemplateDTO() {
        return MapTemplateDTO.builder()
                .name("name")
                .attributes(new HashSet<>())
                .validations(new ArrayList<>())
                .build();
    }

    public UpdateTemplate createUpdateTemplate() {
        return UpdateTemplate.builder()
                .oldName("name")
                .newName("name")
                .createdBy("createdBy")
                .newAttributes(new HashSet<>())
                .newValidations(new ArrayList<>())
                .build();
    }

    public static Document createTemplateAttribute() {
        Document document = new Document();
        document.append("_id", "name");
        document.append("attributes", new ArrayList<>());
        document.append("validations", new ArrayList<>());
        return document;
    }

    public TemplateTableResult createTemplateTableResult() {
        return TemplateTableResult.builder()
                .templatesCount(1)
                .page(List.of(createRegisterTemplate()))
                .build();
    }

    private TableResult createTemplatesTableResult() {
        return TableResult.builder()
                .countResult(List.of(new Document("totalCount", 1)))
                .paginationResult(List.of(createTemplateAttribute()))
                .build();
    }

    private RegisterTemplate createRegisterTemplate() {
        return RegisterTemplate.builder()
                .name("name")
                .attributes(new HashSet<>())
                .validations(new ArrayList<>())
                .build();

    }

    private DBTemplate createDBTemplate() {
        return DBTemplate.builder()
                .name("name")
                .createdBy("createdBy")
                .attributes(new HashSet<>())
                .validations(new ArrayList<>())
                .build();
    }

    private SecurityUser createSecurityUser() {
        return SecurityUser.builder()
                .username("createdBy")
                .password("password")
                .enabled(true)
                .role(Role.FLIGHT_MANAGER_ROLE.name())
                .company("test")
                .build();
    }
}
