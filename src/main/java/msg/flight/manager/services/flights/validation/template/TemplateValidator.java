package msg.flight.manager.services.flights.validation.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.dtos.flights.templates.ValidationDTO;
import msg.flight.manager.persistence.repositories.ValidationRepository;
import msg.flight.manager.services.flights.validation.template.predefinedTypes.PredefinedTypeValidator;
import org.bson.BsonDocument;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TemplateValidator {
    @Autowired
    private PredefinedTypeValidator predefinedValidator;
    @Autowired
    private ValidationRepository validationRepository;
    private static final AttributeValidatorFactory attributeValidatorFactory = new AttributeValidatorFactory();

    public void validate(JsonNode flight, TemplateDTO template) {
        validateContainsRequiredTemplateAttributes(template.getAttributes(), flight);
        StringBuilder exceptionMessage = new StringBuilder();
        for (ValidationDTO validationRule : template.getValidations()) {
            BsonDocument bsonDocument = BsonDocument.parse(validationRule.getJson());
            String type = bsonDocument.getString("type").getValue();
            AttributeValidator attributeValidator = attributeValidatorFactory.createValidator(type);
            ObjectMapper objectMapper = new ObjectMapper();
            String flightJsonString;
            try {
                flightJsonString = objectMapper.writeValueAsString(flight);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Something went wrong when parsing the json flight");
            }
            JsonObject flightObject = new JsonObject(flightJsonString);
            exceptionMessage.append(attributeValidator.validate(bsonDocument, flightObject));
        }
        if (!exceptionMessage.isEmpty()) {
            throw new RuntimeException(exceptionMessage.toString());
        }
    }

    private void validateContainsRequiredTemplateAttributes(Set<TemplateAttribute> attributes, JsonNode flight) {
        for (TemplateAttribute attribute : attributes) {
            if (attribute.isRequired() && !flight.has(attribute.getName())) {
                throw new RuntimeException("Invalid flight for template");
            } else {
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
                    case "airport":
                        predefinedValidator.checkAirport(flight, attribute.getName(), validationRepository);
                        break;
                    case "plane":
                        predefinedValidator.checkPlane(flight, attribute.getName(), validationRepository);
                        break;
                    case "user":
                        predefinedValidator.checkUser(flight, attribute.getName(), validationRepository);
                        break;
                    case "company":
                        predefinedValidator.checkCompany(flight, attribute.getName(), validationRepository);
                        break;
                }
            }
        }
        validationRepository.checkExistentValues();
        validationRepository.cleanValidationValues();
    }
}
