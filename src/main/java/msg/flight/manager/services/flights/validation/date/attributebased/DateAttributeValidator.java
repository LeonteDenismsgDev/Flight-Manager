package msg.flight.manager.services.flights.validation.date.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.date.typebased.DateRuleValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class DateAttributeValidator implements AttributeValidator {
    private static final AttributeExtractor attributeExtractor = new AttributeExtractor();

    @Override
    public String validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        LocalDateTime attributeFloatValue = attributeExtractor.extractLocalDateTimeAttribute(attributeLink, flight);
        try {
            DateRuleValidator dateRuleValidator = extractTextValidator(validationRule, flight);
            return dateRuleValidator.validate(attributeFloatValue,attributeLink);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private DateRuleValidator extractTextValidator(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        DateRuleValidator dateValidationRules = new DateRuleValidator();
        for (Field field : dateValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if(validationRule != null) {
                if (validationRule.isString()) {
                    String linkAttributes = validationRule.asString().getValue();
                    if(linkAttributes.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z")){
                        OffsetDateTime offsetDateTime = OffsetDateTime.parse(linkAttributes);
                        LocalDateTime date  =  offsetDateTime.toLocalDateTime();
                        field.set(dateValidationRules, date);
                    }else{
                        LocalDateTime ruleFloatAttributeValue = attributeExtractor.extractLocalDateTimeAttribute(linkAttributes, flight);
                        field.set(dateValidationRules, ruleFloatAttributeValue);
                    }
                }
            }
        }
        return dateValidationRules;
    }
}
