package msg.flight.manager.services.flights.validation.predefinedTypes;

import com.fasterxml.jackson.databind.JsonNode;
import msg.flight.manager.persistence.repositories.ValidationRepository;
import msg.flight.manager.persistence.repositories.WorkHoursRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class PredefinedValidator {
    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private WorkHoursRepository workHoursRepository;

    public String checkAirport(JsonNode flight, String jsonKey) {
        JsonNode departure = flight.get(jsonKey);
        if (departure.isObject()) {
            if (departure.has("icao") && departure.has("location")) {
                JsonNode icao = departure.get("icao");
                if (icao.isTextual()) {
                    Document dbAirport = validationRepository.getObject("airports", icao.asText());
                    if (dbAirport == null) {
                        return "There is no similar airport registered \n";
                    }
                } else {
                    return "Invalid " + jsonKey + "  airport\n";
                }
            } else {
                return "Invalid " + jsonKey + "  airport\n";
            }
        } else {
            return jsonKey + " should be a valid airport\n";
        }
        return "";
    }

    public String checkForTime(JsonNode flight, String jsonKey) {
        JsonNode dateTime = flight.get(jsonKey);
        if (dateTime.isTextual()) {
            String dt = dateTime.asText();
            if (!dt.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z")) {
                return jsonKey + " needs to be a date\n";
            }
        } else {
            return "Invalid type for " + jsonKey + "\n";
        }
        return "";
    }


    public String checkForPlane(JsonNode flight, String jsonKey) {
        JsonNode plane = flight.get(jsonKey);
        if (plane.isObject()) {
            if (plane.has("registrationNumber")) {
                JsonNode registrationNumber = plane.get("registrationNumber");
                if (registrationNumber.isTextual()) {
                    Document dbPlane = validationRepository.getObject("planes", registrationNumber.asText());
                    if (dbPlane == null) {
                        return "There is  no similar plane\n";
                    }
                } else {
                    return "invalid" + jsonKey + "\n";
                }
            } else {
                return "Invalid " + jsonKey + "\n";
            }
        } else {
            return jsonKey + " should be a valid plane\n";
        }
        return "";
    }

    public String checkForCrew(JsonNode flight, String jsonKey) {
        OffsetDateTime offsetDateTimeArr = OffsetDateTime.parse(flight.get("arrivalTime").asText());
        LocalDateTime startTime  =  offsetDateTimeArr.toLocalDateTime();
        OffsetDateTime offsetDateTimeDep = OffsetDateTime.parse( flight.get("departureTime").asText());
        LocalDateTime endTime  =  offsetDateTimeDep.toLocalDateTime();
        String departure = flight.get("departure").get("location").asText();
        List<String> availableUsers = workHoursRepository.findAvailableUsers(startTime,endTime,departure);
        JsonNode crew = flight.get(jsonKey);
        if (crew.isArray()) {
            for (JsonNode userNode : crew) {
                if (userNode.isObject()) {
                    if (userNode.has("username")) {
                        JsonNode jsonUsername = userNode.get("username");
                        if (jsonUsername.isTextual()) {
                            String username = jsonUsername.asText();
                            Document dbUser = validationRepository.getObject("users", username);
                            if (dbUser == null) {
                                return "There is  no user with the username " + username +"\n";
                            } else {
                                if(!availableUsers.contains(username)){
                                    return "The user with the  username " + username + " is  not available during the flight\n";
                                }
                            }
                        } else {
                            return "Invalid crew list\n";
                        }
                    } else {
                        return "Invalid crew list\n";
                    }
                } else {
                    return "Invalid crew list\n";
                }
            }
        } else {
            return "You should have a list users  for the crew\n";
        }
        return  "";
    }

    public String checkFlightInterval(JsonNode flight, String textError) {
        if(!textError.contains("time")){
            OffsetDateTime offsetDateTimeArr = OffsetDateTime.parse(flight.get("arrivalTime").asText());
            LocalDateTime arrivalTime  =  offsetDateTimeArr.toLocalDateTime();
            OffsetDateTime offsetDateTimeDep = OffsetDateTime.parse( flight.get("departureTime").asText());
            LocalDateTime departureTime  =  offsetDateTimeDep.toLocalDateTime();
            if(!departureTime.isBefore(arrivalTime)){
                return "Arrival time should be after the departure time\n";
            }
        }
        return "";
    }

    public String checkAirports(JsonNode flight, String textError) {
        if(!textError.contains("airport")){
             String departureId = flight.get("departure").get("icao").asText();
             String destinationId = flight.get("destination").get("icao").asText();
             if(destinationId.equals(departureId)){
                 return "Destination airport can't be the same as the departure airport\n";
             }
        }
        return "";
    }
}
