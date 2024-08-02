package msg.flight.manager.services.flights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.repositories.FlightRepository;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.flights.validation.FlightValidator;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlightService {
    @Autowired
    private FlightValidator validator;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public ResponseEntity<String> saveFlight(JsonNode flight, String templateName) {
        String loggedUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        TemplateDTO template = templateRepository.findTemplate(templateName);
        validator.validate(flight, template);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(flight, Map.class);
        map.put("editor", loggedUser);
        map.put("type", "attribute");
        map.put("template", templateName);
        Map<String, Object> savedFlight = flightRepository.save(map);
        Map<String, Object> updateMap = mapper.convertValue(flight, Map.class);
        updateMap.put("type", "attribute" + LocalDateTime.now().toString());
        updateMap.put("template", templateName);
        updateMap.put("editor", loggedUser);
        flightRepository.save(updateMap);
        if (savedFlight != null) {
            return ResponseEntity.ok("saved");
        } else {
            return new ResponseEntity<>("Cold not save the flight", HttpStatus.BAD_REQUEST);
        }
    }

    public List<Map<String, Object>> getFlights(Integer page, Integer size) {
        List<Document> docFlights = flightRepository.getFlights(page, size);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : docFlights) {
            ObjectId objectId = doc.getObjectId("_id");
            doc.put("id", objectId.toHexString());
            doc.remove("_id");
            doc.remove("editor");
            doc.remove("type");
            doc.remove("template");
            result.add(doc);
        }
        return result;
    }

    public ResponseEntity<String> deleteFlight(String flightId) {
        long deleteCount = flightRepository.deleteFlight(flightId);
        if (deleteCount > 0) {
            return ResponseEntity.ok("Flight Deleted");
        } else {
            return new ResponseEntity<>("Could not delete the flight", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<String> updateFlight(JsonNode flight, String flightId) {
        String loggedUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Document flightDoc = flightRepository.findFlightById(flightId);
        if (flightDoc == null) {
            throw new RuntimeException("There is  no flight  with  this id");
        }
        String templateName = flightDoc.get("template").toString();
        TemplateDTO template = templateRepository.findTemplate(templateName);
        if (template == null) {
            throw new RuntimeException("There is no template");
        }
        validator.validate(flight, template);
        ObjectMapper mapper = new ObjectMapper();
        Set<String> keys =  Document.parse(flight.toString()).keySet();
        for (String key : keys) {
            if (!(key.equals("id") || key.equals("template") || key.equals("editor") || key.equals("type"))) {
                if (flightDoc.containsKey(key)) {
                    flightDoc.remove(key);
                }
                flightDoc.put(key, flight.get(key));
            }
        }
        flightDoc.remove("_id");
        flightDoc.remove("editor");
        flightDoc.put("editor", loggedUser);
        Map<String, Object> updateMap = mapper.convertValue(flightDoc, Map.class);
        flightRepository.updateFlight(updateMap, flightId);
        updateMap.put("type", "attribute" + LocalDateTime.now().toString());
        flightRepository.save(updateMap);
        return ResponseEntity.ok("Flight Updated");
    }
}
