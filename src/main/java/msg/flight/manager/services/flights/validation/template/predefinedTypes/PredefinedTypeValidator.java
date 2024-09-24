package msg.flight.manager.services.flights.validation.template.predefinedTypes;

import com.fasterxml.jackson.databind.JsonNode;
import msg.flight.manager.persistence.repositories.ValidationRepository;
import org.springframework.stereotype.Component;

@Component
public class PredefinedTypeValidator {

    public void checkAirport(JsonNode flight, String jsonKey, ValidationRepository validationRepository) {
        JsonNode airport = flight.get(jsonKey);
        if (airport.isObject()) {
            if (airport.has("icao")) {
                JsonNode icao = airport.get("icao");
                if (icao.isTextual()) {
                    validationRepository.addValeForPredefinedCollectionCriteria(airport, icao.asText(), "airports");
                } else {
                    throw new RuntimeException("Invalid " + jsonKey + " airport\n");
                }
            } else {
                throw new RuntimeException("Invalid " + jsonKey + " airport\n");
            }
        } else {
            throw new RuntimeException(jsonKey + " should be a valid airport\n");
        }
    }


    public void checkPlane(JsonNode flight, String jsonKey, ValidationRepository validationRepository) {
        JsonNode plane = flight.get(jsonKey);
        if (plane.isObject()) {
            if (plane.has("registrationNumber")) {
                JsonNode registrationNumber = plane.get("registrationNumber");
                if (registrationNumber.isTextual()) {
                    validationRepository.addValeForPredefinedCollectionCriteria(plane, registrationNumber.asText(), "planes");
                } else {
                    throw new RuntimeException("Invalid " + jsonKey + "\n");
                }
            } else {
                throw new RuntimeException("Invalid " + jsonKey + "\n");
            }
        } else {
            throw new RuntimeException(jsonKey + " should be a valid plane\n");
        }
    }

    public void checkUser(JsonNode flight, String jsonKey, ValidationRepository validationRepository) {
        JsonNode user = flight.get(jsonKey);
        if (user.isObject()) {
            if (user.has("username")) {
                JsonNode username = user.get("username");
                if (username.isTextual()) {
                    validationRepository.addValeForPredefinedCollectionCriteria(user, username.asText(), "users");
                } else {
                    throw new RuntimeException("Invalid " + jsonKey + "\n");
                }
            } else {
                throw new RuntimeException("Invalid " + jsonKey + "\n");
            }
        } else {
            throw new RuntimeException(jsonKey + " should be a valid user\n");
        }
    }

    public void checkCompany(JsonNode flight, String jsonKey, ValidationRepository validationRepository) {
        JsonNode company = flight.get(jsonKey);
        if (company.isObject()) {
            if (company.has("company")) {
                JsonNode companyName = company.get("company");
                if (companyName.isTextual()) {
                    validationRepository.addValeForPredefinedCollectionCriteria(company, companyName.asText(), "companies");
                } else {
                    throw new RuntimeException("Invalid " + jsonKey + "\n");
                }
            } else {
                throw new RuntimeException("Invalid " + jsonKey + "\n");
            }
        } else {
            throw new RuntimeException(jsonKey + " should be a valid company\n");
        }
    }
}
