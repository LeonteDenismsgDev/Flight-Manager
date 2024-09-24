package msg.flight.manager.persistence.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import msg.flight.manager.persistence.dtos.flights.flights.*;
import msg.flight.manager.persistence.enums.FlightState;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.security.SecurityUser;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FlightMapper {

    public DBFlight registerFlightDTOToDBFlight(RegisterFlightDTO flightDTO, DBAirport destination,
                                                DBAirport departure, String company, SecurityUser loggedUser) {
        return DBFlight.builder()
                .arrivalTime(flightDTO.getScheduledArrivalTime())
                .departureTime(flightDTO.getScheduledDepartureTime())
                .scheduledArrivalTime(flightDTO.getScheduledArrivalTime())
                .scheduledDepartureTime(flightDTO.getScheduledDepartureTime())
                .departure(createFlightAirportDTO(flightDTO.getDeparture(), departure))
                .destination(createFlightAirportDTO(flightDTO.getDestination(), destination))
                .template(flightDTO.getTemplate())
                .templateAttributes(jsonNodeToMap(flightDTO.getTemplateAttributes()))
                .state(determineState(FlightState.PENDING.name(), flightDTO.getCrew(), flightDTO.getPlane()))
                .company(company)
                .generated(false)
                .editor(createFlightUserDTO(loggedUser))
                .recurrenceNumber(0)
                .crew(new ArrayList<>(flightDTO.getCrew()))
                .plane(flightDTO.getPlane())
                .build();
    }

    public FlightDescriptionDTO dbFlightToFlightDescriptionDTO(DBFlight flight) {
        return FlightDescriptionDTO.builder()
                .arrivalTime(flight.getArrivalTime())
                .departureTime(flight.getDepartureTime())
                .scheduledArrivalTime(flight.getScheduledArrivalTime())
                .scheduledDepartureTime(flight.getScheduledDepartureTime())
                .destination(flight.getDestination())
                .departure(flight.getDeparture())
                .flightPlaneDTO(flight.getPlane())
                .crew(flight.getCrew())
                .templateAttributes(flight.getTemplateAttributes())
                .state(flight.getState())
                .recurrenceId(flight.getRecursionId())
                .recurrenceNumber(flight.getRecurrenceNumber())
                .editor(flight.getEditor())
                .template(flight.getTemplate())
                .company(flight.getCompany())
                .build();
    }

    private FlightUserDTO createFlightUserDTO(SecurityUser user) {
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
                result.put(field.getKey(), valueNode.toString());  // Handle array case appropriately
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

    public DBFlight updateFlightDTOToDBFlight(DBFlight flight, ValidatedFlightAirports validatedAirports,
                                              UpdateFlightDTO updateFlightDTO) {
        DBFlight dbFlight = DBFlight.builder()
                .arrivalTime(updateFlightDTO.getArrivalTime())
                .departureTime(updateFlightDTO.getDepartureTime())
                .scheduledArrivalTime(updateFlightDTO.getScheduledArrivalTime())
                .scheduledDepartureTime(updateFlightDTO.getScheduledDepartureTime())
                .departure(createFlightAirportDTO(validatedAirports.getDeparture().getIcao(), validatedAirports.getDeparture()))
                .destination(createFlightAirportDTO(validatedAirports.getDestination().getIcao(), validatedAirports.getDestination()))
                .template(flight.getTemplate())
                .templateAttributes(jsonNodeToMap(updateFlightDTO.getTemplateAttributes()))
                .state(determineState(flight.getState(), updateFlightDTO.getCrew(), updateFlightDTO.getPlane()))
                .company(flight.getCompany())
                .generated(false)
                .editor(flight.getEditor())
                .recurrenceNumber(flight.getRecurrenceNumber())
                .crew(new ArrayList<>(updateFlightDTO.getCrew()))
                .plane(updateFlightDTO.getPlane())
                .build();
        dbFlight.setFlightNumber(flight.getFlightNumber());
        dbFlight.setType(flight.getType());
        return dbFlight;
    }

    public DBFlight registerRecurrenceFlightDTOToDBFlight(RegisterRecurrenceFlight recurrenceFlight,
                                                          ValidatedFlightAirports validatedAirports, String company, SecurityUser user,
                                                          String state) {
        return DBFlight.builder()
                .arrivalTime(recurrenceFlight.getScheduledArrivalTime())
                .departureTime(recurrenceFlight.getDepartureTime())
                .scheduledArrivalTime(recurrenceFlight.getScheduledArrivalTime())
                .scheduledDepartureTime(recurrenceFlight.getScheduledDepartureTime())
                .departure(createFlightAirportDTO(validatedAirports.getDeparture().getIcao(), validatedAirports.getDeparture()))
                .destination(createFlightAirportDTO(validatedAirports.getDestination().getIcao(), validatedAirports.getDestination()))
                .template(recurrenceFlight.getTemplate())
                .templateAttributes(jsonNodeToMap(recurrenceFlight.getTemplateAttributes()))
                .state(determineState(state, recurrenceFlight.getCrew(), recurrenceFlight.getPlane()))
                .company(company)
                .generated(false)
                .editor(createFlightUserDTO(user))
                .recurrenceNumber(recurrenceFlight.getRecurrenceNumber())
                .crew(new ArrayList<>(recurrenceFlight.getCrew()))
                .plane(recurrenceFlight.getPlane())
                .build();
    }

    private String determineState(String existentState, Set<FlightUserDTO> crew, FlightPlaneDTO plane) {
        if (plane == null || crew == null) {
            return existentState;
        } else {
            if (existentState.equals(FlightState.PENDING.name())) {
                return FlightState.UPCOMING.name();
            } else {
                return existentState;
            }
        }
    }
}
