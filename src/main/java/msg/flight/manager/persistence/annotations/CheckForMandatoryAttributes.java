package msg.flight.manager.persistence.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckForMandatoryAttributes implements ConstraintValidator<DoesNotHaveMandatoryAttributes, Set<TemplateAttribute>> {
    private static final List<String> mandatoryAttributes = List.of("destination", "departure", "arrivalTime", "departureTime", "plane", "crew");

    @Override
    public boolean isValid(Set<TemplateAttribute> templateAttributes, ConstraintValidatorContext constraintValidatorContext) {
        if(templateAttributes == null || templateAttributes.isEmpty()){
            return false;
        }
        Set<String> attributesNames = templateAttributes.stream().map(TemplateAttribute::getName).collect(Collectors.toSet());
        for (String attribute : mandatoryAttributes) {
            if (attributesNames.contains(attribute)) {
                return false;
            }
        }
        return true;
    }
}
