package msg.flight.manager.persistence.mappers;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import msg.flight.manager.persistence.dtos.flights.flights.FlightDescriptionDTO;
import msg.flight.manager.persistence.enums.FlightState;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.persistence.models.flights.DBRecurrence;
import msg.flight.manager.services.extractors.FlightDocumentAttributeExtractor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Getter
public class DocumentMapper {
    @Autowired
    private FlightDocumentAttributeExtractor attributeExtractor;

    public FlightDescriptionDTO recurrenceDocumentToFlightDescriptionDTO(Document document, LocalDateTime arrivalTime,
                                                                         LocalDateTime departureTime,Integer recurrenceNumber) {
        return FlightDescriptionDTO.builder()
                .scheduledDepartureTime(departureTime)
                .scheduledArrivalTime(arrivalTime)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .destination(attributeExtractor.extractFlightAirportDTO("destination",document))
                .departure(attributeExtractor.extractFlightAirportDTO("departure",document))
                .templateAttributes(attributeExtractor.extractTemplateAttributesMap("templateAttributes",document))
                .state(FlightState.PENDING.name())
                .recurrenceId(document.getString("recursionId"))
                .recurrenceNumber(recurrenceNumber)
                .editor(attributeExtractor.extractFlightUserDTO("editor",document))
                .build();
    }

    public FlightDescriptionDTO flightDocumentToFlightDescriptionDTO(Document flightDocument) {
        return FlightDescriptionDTO.builder()
                .arrivalTime(attributeExtractor.extractLocalDateTime("arrivalTime",flightDocument))
                .departureTime(attributeExtractor.extractLocalDateTime("departureTime",flightDocument))
                .scheduledArrivalTime(attributeExtractor.extractLocalDateTime("scheduledArrivalTime",flightDocument))
                .scheduledDepartureTime(attributeExtractor.extractLocalDateTime("scheduledDepartureTime",flightDocument))
                .destination(attributeExtractor.extractFlightAirportDTO("destination",flightDocument))
                .departure(attributeExtractor.extractFlightAirportDTO("departure",flightDocument))
                .flightPlaneDTO(attributeExtractor.extractFlightPlaneDTO("plane",flightDocument))
                .crew(attributeExtractor.extractFlightUserDTOList("crew",flightDocument))
                .templateAttributes(attributeExtractor.extractTemplateAttributesMap("templateAttributes",flightDocument))
                .state(flightDocument.getString("state"))
                .recurrenceId(flightDocument.getString("recursionId"))
                .recurrenceNumber(flightDocument.getInteger("recurrenceNumber"))
                .editor(attributeExtractor.extractFlightUserDTO("editor",flightDocument))
                .build();
    }
    public DBFlight documentToDBFlight(@NotNull Document document){
        attributeExtractor.checkAttribute(document, List.of("flightNumber","type"));
        DBFlight flight =  DBFlight.builder()
                .arrivalTime(attributeExtractor.extractLocalDateTime("arrivalTime",document))
                .departureTime(attributeExtractor.extractLocalDateTime("departureTime",document))
                .scheduledArrivalTime(attributeExtractor.extractLocalDateTime("scheduledArrivalTime",document))
                .scheduledDepartureTime(attributeExtractor.extractLocalDateTime("scheduledDepartureTime",document))
                .destination(attributeExtractor.extractFlightAirportDTO("destination",document))
                .departure(attributeExtractor.extractFlightAirportDTO("departure",document))
                .template(document.getString("template"))
                .templateAttributes(attributeExtractor.extractTemplateAttributesMap("templateAttributes",document))
                .state(document.getString("state"))
                .recursionId(document.getString("recursionId"))
                .plane(attributeExtractor.extractFlightPlaneDTO("plane",document))
                .crew(attributeExtractor.extractFlightUserDTOList("crew",document))
                .company(document.getString("company"))
                .editor(attributeExtractor.extractFlightUserDTO("editor",document))
                .recurrenceNumber(document.getInteger("recurrenceNumber"))
                .generated(document.getBoolean("generated"))
                .build();
        flight.setFlightNumber(document.getString("flightNumber"));
        flight.setType(document.getString("type"));
        return flight;
    }

    public DBRecurrence documentToDBRecurrence(@NotNull Document document){
        attributeExtractor.checkAttribute(document,List.of("recursionId","template","type","recurrencePattern"));
        return DBRecurrence.builder()
                .recursionId(document.getString("recursionId"))
                .startRecursivePeriod(attributeExtractor.extractLocalDateTime("startRecursivePeriod",document))
                .endRecursivePeriod(attributeExtractor.extractLocalDateTime("endRecursiveTimePeriod",document))
                .scheduledDepartureTime(attributeExtractor.extractLocalDateTime("scheduledDepartureTime",document))
                .scheduledArrivalTime(attributeExtractor.extractLocalDateTime("scheduledArrivalTime",document))
                .destination(attributeExtractor.extractFlightAirportDTO("destination",document))
                .departure(attributeExtractor.extractFlightAirportDTO("departure",document))
                .template(document.getString("template"))
                .templateAttributes(attributeExtractor.extractTemplateAttributesMap("templateAttributes",document))
                .type(document.getString("type"))
                .recurrencePattern(document.getString("recurrencePattern"))
                .editor(attributeExtractor.extractFlightUserDTO("editor",document))
                .build();
    }
}
