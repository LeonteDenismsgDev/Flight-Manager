package msg.flight.manager.services.flights.validation.flight;

import msg.flight.manager.persistence.dtos.flights.flights.FlightPlaneDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightUserDTO;
import msg.flight.manager.persistence.dtos.flights.flights.ValidationFlightDTO;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.mappers.ValidatedFlightAirports;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.repositories.AirportRepository;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import msg.flight.manager.persistence.repositories.TemplateRepository;
import msg.flight.manager.persistence.repositories.WorkHoursRepository;
import msg.flight.manager.services.flights.validation.template.TemplateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FlightValidator {

    @Autowired
    private TemplateValidator templateValidator;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private WorkHoursRepository workHoursRepository;
    @Autowired
    private PlaneRepository planeRepository;

    public ValidatedFlightAirports validate(ValidationFlightDTO flightDTO) {
        if(flightDTO.getScheduledDepartureTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Invalid scheduled departure time");
        }
        if (flightDTO.getScheduledDepartureTime().with(LocalTime.MIN).isEqual(LocalDateTime.now().with(LocalTime.MIN))) {
            if (flightDTO.getCrew() == null || flightDTO.getPlane() == null || flightDTO.getCrew().isEmpty()) {
                throw new RuntimeException("Can't program  a flight for today without a crew and a plane  assigned");
            } else {
                DBPlane plane = planeRepository.get(flightDTO.getPlane().getRegistrationNumber());
                if(plane == null){
                    throw new RuntimeException("Invalid applied plane");
                }
                List<String> availableUsers = workHoursRepository.findAvailableUsers(flightDTO.getScheduledDepartureTime(),
                        flightDTO.getScheduledArrivalTime(), flightDTO.getDeparture());
                Set<String> uniqueAvailableUsers = new HashSet<>(availableUsers);
                Set<String> assignedUsers = flightDTO.getCrew().stream().map(FlightUserDTO::getUsername).collect(Collectors.toSet());
                if (!uniqueAvailableUsers.containsAll(assignedUsers)) {
                    throw new RuntimeException("Unavailable crew members!");
                }
            }
        }
        TemplateDTO template = templateRepository.findTemplate(flightDTO.getTemplate());
        if (flightDTO.getDeparture().equals(flightDTO.getDestination())) {
            throw new RuntimeException("The flight's departure  airport  can't  be the same with the destination airport");
        }
        if (!flightDTO.getScheduledArrivalTime().isAfter(flightDTO.getScheduledDepartureTime())) {
            throw new RuntimeException("Invalid scheduled period for the flight");
        }
        DBAirport destination = airportRepository.get(flightDTO.getDestination());
        DBAirport departure = airportRepository.get(flightDTO.getDeparture());
        if(template == null || destination == null || departure == null){
            throw new RuntimeException("Invalid flight data");
        }
        templateValidator.validate(flightDTO.getTemplateAttributes(), template);
        return new ValidatedFlightAirports(destination, departure);
    }
}
