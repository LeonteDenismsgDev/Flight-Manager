package msg.flight.manager.services.flights.validation.date.validators.typebased;

import java.time.LocalDateTime;

public class DateValidator {
    public boolean  isBefore(LocalDateTime value, LocalDateTime reference){
        return  value.isBefore(reference);
    }

    public boolean isAfter(LocalDateTime value, LocalDateTime reference){
        return value.isAfter(reference);
    }

    public boolean isEqual(LocalDateTime value, LocalDateTime reference){
        return value.isEqual(reference);
    }

    public boolean  isInInterval(LocalDateTime value, LocalDateTime startDate, LocalDateTime endDate){
        return value.isAfter(startDate) && value.isBefore(endDate);
    }
}
