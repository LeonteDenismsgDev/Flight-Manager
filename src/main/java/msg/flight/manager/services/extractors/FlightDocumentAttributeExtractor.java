package msg.flight.manager.services.extractors;

import jakarta.validation.constraints.NotNull;
import msg.flight.manager.persistence.dtos.flights.flights.FlightAirportDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightPlaneDTO;
import msg.flight.manager.persistence.dtos.flights.flights.FlightUserDTO;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FlightDocumentAttributeExtractor {

    public static final String DATA_EXTRACTION_ERROR = "An error appear while extracting the data";

    public void checkAttribute(@NotNull Document document, List<String> attributeNames) {
        for (String attribute : attributeNames) {
            if (!document.containsKey(attribute)) {
                throw new RuntimeException(DATA_EXTRACTION_ERROR);
            }
        }
    }

    private FlightUserDTO documentToFlightUserDTO(Document document) throws ClassCastException {
        checkAttribute(document, List.of("username", "firstName", "lastName", "contactData"));
        Document contactData = document.get("contactData", Document.class);
        Map<String, String> userContactData = documentToMap(contactData, String.class);
        return FlightUserDTO.builder()
                .username(document.getString("username"))
                .firstName(document.getString("firstName"))
                .lastName(document.getString("lastName"))
                .contactData(userContactData)
                .build();
    }

    private <T> Map<String, T> documentToMap(@NotNull Document document, @NotNull Class<T> returnClass) throws ClassCastException{
        Map<String, T> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            T value = returnClass.cast(entry.getValue());
            map.put(entry.getKey(), value);
        }
        return map;
    }

    public LocalDateTime extractLocalDateTime(@NotNull String attributeName, @NotNull Document document) {
        checkAttribute(document, List.of(attributeName));
        try {
            Date extractedDate = document.getDate(attributeName);
            return LocalDateTime.ofInstant(extractedDate.toInstant(), ZoneId.systemDefault());
        } catch (ClassCastException e) {
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }

    }

    public FlightAirportDTO extractFlightAirportDTO(@NotNull String attributeName, @NotNull Document document) {
        try {
            Document airport = document.get(attributeName, Document.class);
            checkAttribute(airport, List.of("icao", "airportName"));
            return FlightAirportDTO.builder()
                    .icao(airport.getString("icao"))
                    .airportName(airport.getString("airportName"))
                    .build();
        } catch (ClassCastException e) {
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }

    }

    public FlightPlaneDTO extractFlightPlaneDTO(@NotNull String attributeName, @NotNull Document document) {
        try {
            Document plane = document.get(attributeName, Document.class);
            checkAttribute(plane, List.of("registrationNumber", "plane"));
            return FlightPlaneDTO.builder()
                    .registrationNumber(plane.getString("registrationNumber"))
                    .model(plane.getString("plane"))
                    .build();
        } catch (ClassCastException e) {
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }

    }

    public List<FlightUserDTO> extractFlightUserDTOList(@NotNull String attributeName, @NotNull Document document) {
        try {
            List<Document> documentList = document.getList(attributeName, Document.class);
            return documentList.stream().map(this::documentToFlightUserDTO).collect(Collectors.toList());
        } catch (ClassCastException | NullPointerException e) {
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }
    }

    public FlightUserDTO extractFlightUserDTO(@NotNull String attribute, @NotNull Document document) {
        try {
            Document user = document.get(attribute, Document.class);
            return documentToFlightUserDTO(user);
        } catch (ClassCastException e) {
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }
    }

    public Map<String,Object> extractTemplateAttributesMap(@NotNull String attributeName,@NotNull Document document){
        try {
            Document templateAttributes = document.get(attributeName, Document.class);
            return documentToMap(templateAttributes, Object.class);
        }catch (ClassCastException e){
            throw new RuntimeException(DATA_EXTRACTION_ERROR);
        }
    }

}
