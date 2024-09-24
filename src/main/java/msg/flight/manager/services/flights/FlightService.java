package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.flights.*;
import msg.flight.manager.persistence.enums.FlightState;
import msg.flight.manager.persistence.mappers.FlightMapper;
import msg.flight.manager.persistence.mappers.RecurrenceMapper;
import msg.flight.manager.persistence.mappers.ValidatedFlightAirports;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.persistence.models.flights.DBRecurrence;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import msg.flight.manager.persistence.repositories.FlightRepository;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.persistence.repositories.utils.criteria.types.FlightsCriteriaBuilder;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.flights.validation.flight.FlightValidator;
import msg.flight.manager.services.generators.FlightsGenerator;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private FlightValidator flightValidator;
    @Autowired
    private SecurityUserUtil securityUserUtil;
    @Autowired
    private FlightMapper flightMapper;
    @Autowired
    private RecurrenceMapper recurrenceMapper;
    @Autowired
    private FlightsGenerator flightsGenerator;
    @Autowired
    private FlightsCriteriaBuilder criteriaBuilder;
    @Autowired
    private TemplateRepository templateRepository;

    @Transactional
    public ResponseEntity<String> saveFlight(RegisterFlightDTO flightDTO) {
        ValidatedFlightAirports flightAirports = flightValidator.validate(flightDTO);
        SecurityUser user = securityUserUtil.getLoggedUser();
        DBFlight flight = flightMapper.registerFlightDTOToDBFlight(flightDTO, flightAirports.getDestination(),
                flightAirports.getDeparture(), user.getCompany(), user);
        if (flightDTO.getRecursivePattern() != null) {
            DBRecurrence savedRecurrence = saveDBRecurrence(flightDTO, flightAirports, user);
            flight.setRecursionId(savedRecurrence.getRecursionId());
        }
        flightRepository.save(flight);
        flightRepository.save(flight.getAuditEntry());
        return ResponseEntity.ok("Flight saved");
    }

    @Transactional
    public List<FlightDescriptionDTO> getDateFlights(GetDateFlightsDTO flightsDTO) {
        SecurityUser loggedUser = securityUserUtil.getLoggedUser();
        List<DBRecurrence> intervalRecurrences = flightRepository.getDBRecurrences(loggedUser.getCompany(), flightsDTO.getStartDateTime());
        Set<String> recurrenceIds = intervalRecurrences.stream().map(DBRecurrence::getRecursionId).collect(Collectors.toSet());
        List<DBFlight> existentRecurrenceFlights = flightRepository.getDBFlights(loggedUser.getCompany(), recurrenceIds);
        List<DBFlight> intervalFlights = flightRepository.getDBFlights(loggedUser.getCompany(), flightsDTO.getStartDateTime(),
                flightsDTO.getEndDateTime());
        for (DBRecurrence recurrence : intervalRecurrences) {
            DBTemplate template = templateRepository.findDBTemplate(recurrence.getTemplate());
            intervalFlights.addAll(flightsGenerator.generateFlights(recurrence, existentRecurrenceFlights, flightsDTO.getStartDateTime(),
                    flightsDTO.getEndDateTime(), template));
            for (DBFlight flight : existentRecurrenceFlights) {
                if (flight.getRecursionId().equals(recurrence.getRecursionId())) {
                    if (flightsGenerator.afterOrEqual(flight.getDepartureTime(), flightsDTO.getStartDateTime()) &&
                            flightsGenerator.beforeOrEquals(flight.getDepartureTime(), flightsDTO.getEndDateTime())) {
                        intervalFlights.add(flight);
                    }
                }
            }
        }
        return intervalFlights.stream().map(flight -> flightMapper.dbFlightToFlightDescriptionDTO(flight)).toList();
    }

    @Transactional
    public ResponseEntity<String> updateFlight(UpdateFlightDTO updateFlightDTO, String flightNumber) {
        ValidatedFlightAirports validatedAirports = flightValidator.validate(updateFlightDTO);
        DBFlight flight = flightRepository.findFlightById(flightNumber);
        if (flight == null) {
            return new ResponseEntity<>("Invalid flight number", HttpStatus.NOT_FOUND);
        }
        DBFlight updatedFlight = flightMapper.updateFlightDTOToDBFlight(flight, validatedAirports, updateFlightDTO);
        flightRepository.save(updatedFlight);
        DBFlight updateAudit = updatedFlight.getAuditEntry();
        flightRepository.save(updateAudit);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> updateNonExistentRecurrenceFlight(RegisterRecurrenceFlight flightRecurrence) {
        DBRecurrence recurrence = flightRepository.findRecurrenceById(flightRecurrence.getRecurrenceId());
        if (recurrence == null) {
            return new ResponseEntity<>("Non existent recurrence", HttpStatus.NOT_FOUND);
        }
        ValidatedFlightAirports validatedAirports = flightValidator.validate(flightRecurrence);
        SecurityUser loggedUser = securityUserUtil.getLoggedUser();
        DBFlight flight = flightMapper.registerRecurrenceFlightDTOToDBFlight(flightRecurrence, validatedAirports, loggedUser.getCompany(),
                loggedUser, FlightState.PENDING.name());
        flightRepository.save(flight);
        flightRepository.save(flight.getAuditEntry());
        return ResponseEntity.ok("Updated");
    }

    public ResponseEntity<String> deleteRecurrence(String id) {
        long deleteCount = flightRepository.deleteRecurrence(id);
        if (deleteCount > 0) {
            return ResponseEntity.ok("Recurrence deleted");
        }
        return new ResponseEntity<>("The recurrence couldn't be deleted", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<String> deleteFlight(String flightId) {
        long deleteCount = flightRepository.deleteFlight(flightId);
        if (deleteCount > 0) {
            return ResponseEntity.ok("Flight Deleted");
        } else {
            return new ResponseEntity<>("Could not delete the flight", HttpStatus.BAD_REQUEST);
        }
    }

    private DBRecurrence saveDBRecurrence(RegisterFlightDTO flightDTO, ValidatedFlightAirports flightAirports, SecurityUser editor) {
        DBRecurrence flightRecurrence = recurrenceMapper.registerFlightDTOToDBRecurrence(flightDTO, flightAirports.getDestination(),
                flightAirports.getDeparture(), editor, editor.getCompany());
        return flightRepository.save(flightRecurrence);
    }
}
