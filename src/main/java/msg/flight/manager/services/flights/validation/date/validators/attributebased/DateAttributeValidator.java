package msg.flight.manager.services.flights.validation.date.validators.attributebased;

import msg.flight.manager.services.flights.validation.AttributeValidator;
import msg.flight.manager.services.flights.validation.date.validators.typebased.DateRuleValidator;
import msg.flight.manager.services.flights.validation.extraction.AttributeExtractor;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DateAttributeValidator implements AttributeValidator {
    @Autowired
    private AttributeExtractor attributeExtractor;

    @Override
    public boolean validate(BsonDocument validationRule, JsonObject flight) {
        String attributeLink = validationRule.getString("attribute").getValue();
        LocalDateTime attributeFloatValue = attributeExtractor.extractLocalDateTimeAttribute(attributeLink, flight);
        try {
            DateRuleValidator dateRuleValidator = extractTextValidator(validationRule,flight);
            return dateRuleValidator.validate(attributeFloatValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private DateRuleValidator extractTextValidator(BsonDocument validationRules, JsonObject flight) throws IllegalAccessException {
        DateRuleValidator dateValidationRules = new DateRuleValidator();
        for (Field field : dateValidationRules.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            BsonValue validationRule = validationRules.get(field.getName());
            if (validationRule.isString()) {
                String linkAttributes = validationRules.asString().getValue();
                LocalDateTime ruleFloatAttributeValue = attributeExtractor.extractLocalDateTimeAttribute(linkAttributes, flight);
                field.set(dateValidationRules, ruleFloatAttributeValue);
            }
            if (validationRule.isDateTime()) {
                Instant instant = Instant.ofEpochMilli(validationRule.asDateTime().getValue());
                field.set(dateValidationRules, LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            }
        }
        return dateValidationRules;
    }
}
