package msg.flight.manager.services.flights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.ValidationDTO;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.repositories.FlightRepository;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class FlightServiceTest {
    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private FlightRepository flightRepository;
    @Mock
    private SecurityUserUtil securityUser;
    @InjectMocks
    private FlightService flightService;
    @Captor
    private ArgumentCaptor<Map<String, Object>> mapCaptor;

    @Test
    public void saveFlight_returnsInvalidTemplate_whenNonexistentTemplate() {
        ResponseEntity<String> expected = new ResponseEntity<>("There is no template from each you want to create the flight", HttpStatus.NOT_FOUND);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
       // Assertions.assertEquals(expected, flightService.saveFlight(null, "test"));
    }

    @Test
    public void saveFlight_returnsSavedResponse_whenFlightSavedSuccessfully() {
        ResponseEntity<String> expected = ResponseEntity.ok("saved");
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(templateRepository.findTemplate("templateTest")).thenReturn(createTemplateDTO());
        //Mockito.when(flightRepository.save(mapCaptor.capture())).thenReturn(new HashMap<>()).thenReturn(new HashMap<>());
        //Assertions.assertEquals(expected, flightService.saveFlight(createJsonFlight(false), "templateTest"));
    }

    @Test
    public void saveFlight_returnsCouldNotSaveResponse_whenRepositoryCouldNotSaveFlight() {
        ResponseEntity<String> expected = new ResponseEntity<>("Cold not save the flight", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(templateRepository.findTemplate("templateTest")).thenReturn(createTemplateDTO());
        //Mockito.when(flightRepository.save(mapCaptor.capture())).thenReturn(null).thenReturn(new HashMap<>());
        //Assertions.assertEquals(expected, flightService.saveFlight(createJsonFlight(false), "templateTest"));
    }

    @Test
    public void saveFlight_returnsCouldNotSaveResponse_whenRepositoryCouldNotSaveFlightHistory() {
        ResponseEntity<String> expected = new ResponseEntity<>("Cold not save the flight", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(templateRepository.findTemplate("templateTest")).thenReturn(createTemplateDTO());
        //Mockito.when(flightRepository.save(mapCaptor.capture())).thenReturn(new HashMap<>()).thenReturn(null);
        //Assertions.assertEquals(expected, flightService.saveFlight(createJsonFlight(false), "templateTest"));
    }

    @Test
    public void getFlights_returnsExpectedResult_whenCalled() {
        List<Document> returnedFlights = List.of(createDocument("_id", true));
        List<Map<String, Object>> expected = new ArrayList<>();
        expected.add(createDocument("id", false));
        //Mockito.when(flightRepository.getDateFlights(0, 2)).thenReturn(returnedFlights);
        //Assertions.assertEquals(expected.get(0).toString(), flightService.getDateFlights(0, 2).get(0).toString());
    }

    @Test
    public void deleteFlight_returnsDeletedResult_whenFlightDeleted() {
        ResponseEntity<String> expected = ResponseEntity.ok("Flight Deleted");
        Mockito.when(flightRepository.deleteFlight("test")).thenReturn(1L);
        Assertions.assertEquals(expected, flightService.deleteFlight("test"));
    }

    @Test
    public void deleteFlight_returnsNotDeletedResult_whenFlightDeletedFails() {
        ResponseEntity<String> expected = new ResponseEntity<>("Could not delete the flight", HttpStatus.BAD_REQUEST);
        Mockito.when(flightRepository.deleteFlight("test")).thenReturn(0L);
        Assertions.assertEquals(expected, flightService.deleteFlight("test"));
    }

    @Test
    public void updateFlight_returnsNoFlightResponse_whenNonexistentFlight() {
        ResponseEntity<String> expected = new ResponseEntity<>("There is no flight  with this id", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(flightRepository.findFlightById("test")).thenReturn(null);
        Assertions.assertEquals(expected, flightService.updateFlight(null, "test"));
    }

    @Test
    public void updateFlight_returnsCouldNotUpdateResult_whenFlightUpdatesFailed() {
        ResponseEntity<String> expected = new ResponseEntity<>("Could  not update flight", HttpStatus.BAD_REQUEST);
        Document flightDoc = createDocument("_id", true);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(flightRepository.findFlightById("test")).thenReturn(flightDoc);
        Mockito.when(flightRepository.updateFlight(Mockito.anyMap(), Mockito.eq("test"))).thenReturn(null);
        Assertions.assertEquals(expected, flightService.updateFlight(createJsonFlight(false), "test"));
    }

    @Test
    public void updateFlight_returnsUpdatedResult_whenFlightUpdated() {
        ResponseEntity<String> expected = ResponseEntity.ok("Flight Updated");
        Document flightDoc = createDocument("_id", true);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(flightRepository.findFlightById("test")).thenReturn(flightDoc);
        Assertions.assertEquals(expected, flightService.updateFlight(createJsonFlight(false), "test"));
        Mockito.verify(flightRepository).updateFlight(mapCaptor.capture(), Mockito.eq("test"));
        Assertions.assertFalse(mapCaptor.getValue().containsKey("_id"));
    }

    @Test
    public void updateFlight_throwsRuntimeException_whenFlightHistoryIsNotUpdated() {
        ResponseEntity<String> expected = new ResponseEntity<>("Could  not update flight", HttpStatus.BAD_REQUEST);
        Document flightDoc = createDocument("_id", true);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(flightRepository.findFlightById("test")).thenReturn(flightDoc);
        //Mockito.when(flightRepository.save(Mockito.anyMap())).thenReturn(null);
       // Assertions.assertThrows(RuntimeException.class, () -> flightService.updateFlight(createJsonFlight(false), "test"));
    }

    @Test
    public void updateFlight_returnsInvalidRequestResponse_whenTriesToUpdateNonValidAttributes() {
        ResponseEntity<String> expected = new ResponseEntity<>("Invalid update request", HttpStatus.BAD_REQUEST);
        Mockito.when(securityUser.getLoggedUser()).thenReturn(createSecurityUser());
        Mockito.when(flightRepository.findFlightById("test")).thenReturn(createDocument("_id", true));
        Assertions.assertEquals(expected, flightService.updateFlight(createJsonFlight(true), "test"));
    }

    private Document createDocument(String idLabel, boolean inCreation) {
        Document departure = new Document("icao", "JYGUD6778")
                .append("airportName", "airportName")
                .append("location", "Location");
        Document destination = new Document("icao", "GSTV767")
                .append("airportName", "airportName")
                .append("location", "Location");
        Document plane = new Document("registrationNumber", "DHBDC63674");
        Document document = new Document()
                .append("departure", departure)
                .append("destination", destination)
                .append("arrivalTime", Instant.parse("2024-08-24T11:52:53.145Z"))
                .append("departureTime", Instant.parse("2024-08-27T11:52:53.145Z"))
                .append("plane", plane)
                .append("crew", List.of(new Document("username", "4fliMfm")))
                .append("newAttribute", 152)
                .append(idLabel, new ObjectId("66acd131208bf11e31baaba7"));
        if (inCreation) {
            document.append("editor", "4fliMfm")
                    .append("type", "attribute")
                    .append("template", "test");
        }
        return document;
    }

    private TemplateDTO createTemplateDTO() {
        return TemplateDTO.builder()
                .name("templateTest")
                .attributes(Set.of(createAttribute()))
                .validations(List.of(createValidation()))
                .build();
    }

    private TemplateAttribute createAttribute() {
        return TemplateAttribute.builder()
                .name("attributeName")
                .label("attributeLabel")
                .required(false)
                .type("text")
                .defaultValue("defaultValue")
                .description("description")
                .build();
    }

    private ValidationDTO createValidation() {
        return new ValidationDTO("{\"attribute\":\"attr\",\"type\":\"number\",\"min\":\"text\"}");
    }

    private JsonNode createJsonFlight(Boolean hasId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        createAirportNode(objectNode, "departure");
        createAirportNode(objectNode, "destination");
        objectNode.put("arrivalTime", "2024-08-27T11:52:53.145Z");
        objectNode.put("departureTime", "2024-08-24T11:52:53.145Z");
        ObjectNode plane = objectNode.putObject("plane");
        plane.put("registrationNumber", "gesfy12756");
        ArrayNode crew = objectNode.putArray("crew");
        ObjectNode user = mapper.createObjectNode();
        user.put("username", "4fliMfm");
        crew.add(user);
        objectNode.put("otherAttribute", "attributeName");
        if (hasId) {
            objectNode.put("id", "flightId");
            objectNode.put("editor", "flightEditor");
            objectNode.put("template", "template");
        }
        return objectNode;
    }

    private static void createAirportNode(ObjectNode objectNode, String airportLabel) {
        ObjectNode departure = objectNode.putObject(airportLabel);
        departure.put("icao", "airportId");
        departure.put("airportName", "airportName");
        departure.put("location", "airportLocation");
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
}
