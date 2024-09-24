package msg.flight.manager.persistence.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import msg.flight.manager.persistence.dtos.flights.flights.FlightAirportDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightUserDTO;
import msg.flight.manager.persistence.dtos.flights.flights.RegisterFlightDTO;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.models.flights.DBRecurrence;
import msg.flight.manager.security.SecurityUser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class RecurrenceMapper {
    public DBRecurrence registerFlightDTOToDBRecurrence(RegisterFlightDTO flightDTO, DBAirport destination, DBAirport departure,
                                                        SecurityUser editor,String company) {
        return DBRecurrence.builder()
                .startRecursivePeriod(flightDTO.getScheduledDepartureTime())
                .endRecursivePeriod(flightDTO.getRecursivePattern().getRecurrenceEnd())
                .scheduledDepartureTime(flightDTO.getScheduledDepartureTime())
                .scheduledArrivalTime(flightDTO.getScheduledArrivalTime())
                .departure(createFlightAirportDTO(flightDTO.getDeparture(), departure))
                .destination(createFlightAirportDTO(flightDTO.getDestination(), destination))
                .type("recurrence")
                .template(flightDTO.getTemplate())
                .templateAttributes(jsonNodeToMap(flightDTO.getTemplateAttributes()))
                .recurrencePattern(flightDTO.getRecursivePattern().getRecurrencePattern())
                .editor(createFlightUserDTO(editor))
                .company(company)
                .build();
    }

    private FlightUserDTO createFlightUserDTO(SecurityUser user){
       return new FlightUserDTO(user.getUsername(), user.getFirstName(), user.getLastName(), user.getContactData());
    }

    private FlightAirportDTO createFlightAirportDTO(String departureId, DBAirport airport) {
        return new FlightAirportDTO(departureId, airport.getAirportName());
    }

    private Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
        Map<String, Object> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode valueNode = field.getValue();
            if (valueNode.isObject()) {
                result.put(field.getKey(), jsonNodeToMap(valueNode));
            } else if (valueNode.isArray()) {
                result.put(field.getKey(), valueNode.toString());
            } else if (valueNode.isTextual()) {
                result.put(field.getKey(), valueNode.asText());
            } else if (valueNode.isNumber()) {
                result.put(field.getKey(), valueNode.numberValue());
            } else if (valueNode.isBoolean()) {
                result.put(field.getKey(), valueNode.asBoolean());
            } else {
                result.put(field.getKey(), valueNode.toString());
            }
        }
        return result;
    }
}
