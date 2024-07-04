package msg.flight.manager.services.flights.validation.text.validators.typebased;

import java.util.List;

public class StringValidator {
    public boolean isContainedIn(String value,String reference){
        return reference.contains(value);
    }

    public boolean isEqualWith(String value,String reference){
        return reference.equals(value);
    }

    public boolean isAPartOf(String value, List<String> reference){
        return reference.contains(value);
    }
}
