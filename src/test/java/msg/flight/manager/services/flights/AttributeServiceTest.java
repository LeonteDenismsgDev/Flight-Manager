package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.dtos.flights.attributes.RegisterAttribute;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.flights.DBAttribute;
import msg.flight.manager.persistence.repositories.AttributesRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AttributeServiceTest {
    @Mock
    private AttributesRepository attributesRepository;
    @Mock
    private SecurityUserUtil securityUser;
    @InjectMocks
    private AttributeService attributeService;
    @Captor
    private ArgumentCaptor<DBAttribute> attributeCaptor = ArgumentCaptor.forClass(DBAttribute.class);

    @Test
    public void createAttribute_returnsInvalidAttributeResponse_whenNewAttributeInvalidType() {
        ResponseEntity<String> expected = new ResponseEntity<>("Invalid  attribute type", HttpStatus.BAD_REQUEST);
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("invalidType", null)));
    }

    @Test
    public void createAttribute_returnsInvalidDefaultValue_whenNullDefaultValueForObject() {
        ResponseEntity<String> expected = new ResponseEntity<>("You need at least one default value", HttpStatusCode.valueOf(400));
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("OBJECT", null)));
    }

    @Test
    public void createAttribute_returnsInvalidDefaultValueType_whenOtherTypeInsteadOfMap() {
        ResponseEntity<String> expected = new ResponseEntity<>("Default value type incorrect", HttpStatusCode.valueOf(400));
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("object", "other type")));
    }

    @Test
    public void createAttribute_returnsExpectedResponse_whenCorrectDefaultValueObject() {
        ResponseEntity<String> expected = new ResponseEntity<>("saved", HttpStatus.OK);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(attributesRepository.save(attributeCaptor.capture())).thenReturn(new DBAttribute());
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("OBJECT", new LinkedHashMap<>())));
        Assertions.assertEquals(createDBAttribute("object", new LinkedHashMap<>()), attributeCaptor.getValue());
    }

    @Test
    public void createAttribute_returnsUnableToSaveResponse_whenRepositoryCouldNotSaveAttribute(){
        ResponseEntity<String> expected = new ResponseEntity<>("Unable to save the attribute", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("text", "test")));
    }

    @Test
    public void createAttribute_returnsExpectedResponse_whenCorrectDefaultValueText() {
        ResponseEntity<String> expected = new ResponseEntity<>("saved", HttpStatus.OK);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(attributesRepository.save(attributeCaptor.capture())).thenReturn(new DBAttribute());
        Assertions.assertEquals(expected, attributeService.createAttribute(createRegisterAttribute("text", "test")));
        Assertions.assertEquals(createDBAttribute("text","test"), attributeCaptor.getValue());
    }

    @Test
    public void getAppAttributes_returnsExpectedResult_whenCalled(){
        SecurityUser loggedUser = createSecurityUser();
        Mockito.when(securityUser.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(attributesRepository.applicationAttributes(loggedUser.getUsername())).thenReturn(new ArrayList<>());
        Assertions.assertEquals(new ArrayList<>(),attributeService.getAppAttributes());
    }

    @Test
    public void updateAttribute_returnsUnableToUpdate_whenRepositoryCouldNotUpdateAttribute() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Unable to update attribute", HttpStatusCode.valueOf(400));
        Assertions.assertEquals(expected, attributeService.updateAttribute("id",createAttributeDTO()));
    }

    @Test
    public void updateAttribute_returnsUnableToUpdate_whenRepositoryUpdatesAttribute() throws IllegalAccessException {
        ResponseEntity<String> expected = new ResponseEntity<>("Attribute updated", HttpStatusCode.valueOf(200));
        AttributeDTO attributeDTO = createAttributeDTO();
        Mockito.when(attributesRepository.update("id", attributeDTO)).thenReturn(true);
        Assertions.assertEquals(expected, attributeService.updateAttribute("id",attributeDTO));
    }

    @Test
    public void updateAttribute_throwsIllegalAccessException_whenRepositoryThrowsExceptions() throws IllegalAccessException {
        AttributeDTO attributeDTO = createAttributeDTO();
        Mockito.doThrow(IllegalAccessException.class)
                .when(attributesRepository).update("id", attributeDTO);
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            attributeService.updateAttribute("id", attributeDTO);
        });
    }

    @Test
    public void deleteAttribute_returnsDeletedAttribute_whenRepositoryDeletesAttribute(){
        ResponseEntity<String> expected = new ResponseEntity<>("Attribute deleted", HttpStatusCode.valueOf(200));
        Mockito.when(attributesRepository.delete("test")).thenReturn(true);
        Assertions.assertEquals(expected,attributeService.deleteAttribute("test"));
    }

    @Test
    public void deleteAttribute_returnsUnableToDeleteAttribute_whenRepositoryCouldNotDeleteAttribute(){
        ResponseEntity<String> expected = new ResponseEntity<>("Unable to delete attribute", HttpStatusCode.valueOf(400));
        Assertions.assertEquals(expected,attributeService.deleteAttribute("test"));
    }

    private AttributeDTO createAttributeDTO(){
        return AttributeDTO.builder()
                .id("id")
                .label("label")
                .name("name")
                .required(false)
                .globalVisibility(true)
                .type("object")
                .defaultValue(null)
                .searchKeyWords(new ArrayList<>())
                .description("description")
                .editable(true)
                .build();
    }

    private DBAttribute createDBAttribute(String type, Object defaultValue) {
        return DBAttribute.builder()
                .name("testattribute")
                .label("testAttribute")
                .required(false)
                .globalVisibility(true)
                .type(type)
                .defaultValue(defaultValue)
                .createdBy("test")
                .searchKeyWords(List.of("testattribute", type, "testattribute"))
                .description("description")
                .build();
    }

    private SecurityUser createSecurityUser() {
        return SecurityUser.builder()
                .username("test")
                .password("password")
                .enabled(true)
                .role(Role.FLIGHT_MANAGER_ROLE.name())
                .company("test")
                .build();
    }

    private RegisterAttribute createRegisterAttribute(String attributeType, Object defaultValue) {
        return RegisterAttribute.builder().name("testAttribute")
                .type(attributeType)
                .description("description")
                .defaultValue(defaultValue)
                .isGlobal(true)
                .isRequired(false)
                .build();
    }

}
