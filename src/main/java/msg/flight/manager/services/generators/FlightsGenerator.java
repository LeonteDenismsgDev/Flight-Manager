package msg.flight.manager.services.generators;

import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.enums.FlightState;
import msg.flight.manager.persistence.mappers.FlightMapper;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.persistence.models.flights.DBRecurrence;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlightsGenerator {
    @Autowired
    private FlightMapper flightMapper;

    public List<DBFlight> generateFlights(DBRecurrence recurrence, List<DBFlight> flights, LocalDateTime filterStart,
                                          LocalDateTime filterEnd, DBTemplate template) {
        List<DBFlight> result = new ArrayList<>();
        Set<Integer> recurrenceNumbers = flights.stream().map(DBFlight::getRecurrenceNumber).collect(Collectors.toSet());
        RecurrencePattern recurrencePattern = new RecurrencePattern(recurrence.getRecurrencePattern());
        LocalDateTime arrivalTime = recurrence.getScheduledArrivalTime();
        LocalDateTime recurrenceStart = recurrence.getStartRecursivePeriod();
        LocalDateTime recurrenceEnd = recurrence.getEndRecursivePeriod();
        String timeUnit = recurrencePattern.getRecurrenceTimeUnit();
        int recursiveNumber = recurrencePattern.getRecurrenceNumber();
        long timeDifference = beforeOrEquals(filterStart, recurrenceStart) ?
                0 : recurrencePattern.timeDifference(recurrenceStart, filterStart);
        int lastFlightBeforeFilterStart = (int) (timeDifference / recursiveNumber);
        int recurrenceNumber = lastFlightBeforeFilterStart + 1;
        RecurrencePattern lastRecurrenceNumber = new RecurrencePattern(recursiveNumber * recurrenceNumber + timeUnit);
        LocalDateTime departureRecurrenceDay = lastRecurrenceNumber.addTimeUnit(recurrenceStart);
        LocalDateTime arrivalRecurrenceDay = lastRecurrenceNumber.addTimeUnit(arrivalTime);
        LocalDateTime generationEnd = filterEnd.isBefore(recurrenceEnd) ? filterEnd : recurrenceEnd;
        while (beforeOrEquals(departureRecurrenceDay, generationEnd)) {
            if (!recurrenceNumbers.contains(recurrenceNumber)) {
                result.add(createDBFlight(recurrence, departureRecurrenceDay, arrivalRecurrenceDay, template, recurrenceNumber));
            }
            departureRecurrenceDay = recurrencePattern.addTimeUnit(departureRecurrenceDay);
            arrivalRecurrenceDay = recurrencePattern.addTimeUnit(arrivalRecurrenceDay);
            recurrenceNumber++;
        }
        return result;
    }

    public boolean afterOrEqual(LocalDateTime filteredDate, LocalDateTime compareDate) {
        return filteredDate.isAfter(compareDate) || filteredDate.isEqual(compareDate);
    }

    public boolean beforeOrEquals(LocalDateTime filteredDate, LocalDateTime compareDate) {
        return filteredDate.isBefore(compareDate) || filteredDate.isEqual(compareDate);
    }

    private DBFlight createDBFlight(DBRecurrence recurrence, LocalDateTime departureTime, LocalDateTime arrivalTime, DBTemplate template,
                                    Integer recurrenceNumber) {
        return DBFlight.builder()
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .scheduledArrivalTime(arrivalTime)
                .scheduledDepartureTime(departureTime)
                .destination(recurrence.getDestination())
                .departure(recurrence.getDeparture())
                .template(recurrence.getTemplate())
                .templateAttributes(requireMapAttributes(template))
                .state(FlightState.PENDING.name())
                .recursionId(recurrence.getRecursionId())
                .recurrenceNumber(recurrenceNumber)
                .plane(null)
                .crew(new ArrayList<>())
                .company(recurrence.getCompany())
                .editor(recurrence.getEditor())
                .generated(true)
                .build();
    }

    private Map<String, Object> requireMapAttributes(DBTemplate template) {
        Map<String, Object> requiredAttributes = new HashMap<>();
        template.getAttributes().stream().filter(TemplateAttribute::isRequired).forEach(attribute -> {
            requiredAttributes.put(attribute.getName(), attribute.getDefaultValue());
        });
        return requiredAttributes;
    }
}
