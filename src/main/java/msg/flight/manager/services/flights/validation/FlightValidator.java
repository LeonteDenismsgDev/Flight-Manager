package msg.flight.manager.services.flights.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.ValidationDTO;
import msg.flight.manager.services.flights.validation.predefinedTypes.PredefinedValidator;
import org.bson.BsonDocument;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FlightValidator {
    @Autowired
    private PredefinedValidator predefinedValidator;
    private static final AttributeValidatorFactory attributeValidatorFactory = new AttributeValidatorFactory();

    public void validate(JsonNode flight, TemplateDTO template) {
        if(!flight.has("destination") || !flight.has("departure") || !flight.has("arrivalTime") || !flight.has("departureTime") || !flight.has("plane") || !flight.has("crew")){
            throw  new RuntimeException("Invalid flight. A flight should have : destination, departure, arrivalTime, departureTime, a plane and some crew!");
        }
        validateContainsRequiredTemplateAttributes(template.getAttributes(),flight);
        validateRequiredParams(flight);
        String exceptionMessage = "";
        for (ValidationDTO validationRule : template.getValidations()) {
            BsonDocument bsonDocument = BsonDocument.parse(validationRule.getJson());
            String type =bsonDocument.getString("type").getValue();
            AttributeValidator attributeValidator = attributeValidatorFactory.createValidator(type);
            ObjectMapper objectMapper = new ObjectMapper();
            String flightJsonString = null;
            try {
                flightJsonString = objectMapper.writeValueAsString(flight);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Something  went wrong when parsing json flight");
            }
            JsonObject flightObject = new JsonObject(flightJsonString);
            exceptionMessage += attributeValidator.validate(bsonDocument,flightObject);
        }
        if(!exceptionMessage.isEmpty()){
            throw new RuntimeException(exceptionMessage);
        }
    }

    private void validateContainsRequiredTemplateAttributes(Set<TemplateAttribute> attributes, JsonNode flight){
        for(TemplateAttribute attribute: attributes){
            if(attribute.isRequired() && !flight.has(attribute.getName())){
                throw  new RuntimeException("Invalid flight for template");
            }else {
                JsonNode attributeType = flight.get(attribute.getName());
                switch (attribute.getType()) {
                    case "text":
                        if (!attributeType.isTextual()) {
                            throw new RuntimeException("Attribute " + attribute.getName() + " needs to be text\n");
                        }
                        break;
                    case "number":
                        if (!attributeType.isInt()) {
                            throw new RuntimeException("Attribute " + attribute.getName() + " needs to be a number\n");
                        }
                        break;
                    case "precision_number":
                        if (!attributeType.isFloat()) {
                            throw new RuntimeException("Attribute " + attribute.getName() + " needs to be a precision number\n");
                        }
                        break;
                    case "date":
                        if (!(attributeType.isTextual() && attributeType.asText().matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z"))) {
                            throw new RuntimeException("Attribute " + attribute.getName() + " needs to be a date");
                        }
                        break;
                    default:
                        if (!attributeType.isObject()){
                            throw new RuntimeException("Attribute "+ attribute.getName() + " need to be an/a " + attribute.getType() + "\n");
                        }
                }
            }
        }
    }

    private void validateRequiredParams(JsonNode flight) {
        StringBuilder builder = new StringBuilder();
        builder.append(predefinedValidator.checkAirport(flight,"destination"));
        builder.append(predefinedValidator.checkAirport(flight,"departure"));
        builder.append(predefinedValidator.checkForTime(flight,"arrivalTime"));
        builder.append(predefinedValidator.checkForTime(flight,"departureTime"));
        builder.append(predefinedValidator.checkForPlane(flight,"plane"));
        builder.append(predefinedValidator.checkFlightInterval(flight,builder.toString()));
        builder.append(predefinedValidator.checkAirports(flight,builder.toString()));
        String errorMessage = builder.toString();
        if(!errorMessage.isEmpty()){
            throw  new RuntimeException(errorMessage);
        }else{
            builder.setLength(0);
            builder.append(predefinedValidator.checkForCrew(flight,"crew"));
            if(!builder.toString().isEmpty()){
                throw new RuntimeException(builder.toString());
            }
        }
    }
}
