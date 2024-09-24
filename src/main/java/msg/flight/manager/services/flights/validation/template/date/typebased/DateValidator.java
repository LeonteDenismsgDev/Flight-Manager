package msg.flight.manager.services.flights.validation.template.date.typebased;

import java.time.LocalDateTime;

public class DateValidator {
    public boolean  isBefore(LocalDateTime value, LocalDateTime reference){
        return  value.isBefore(reference);
    }

    public boolean isAfter(LocalDateTime value, LocalDateTime reference){
        return value.isAfter(reference);
    }

    public boolean  isInInterval(LocalDateTime value, LocalDateTime startDate, LocalDateTime endDate){
        if(endDate.isBefore(startDate)){
            throw  new RuntimeException("Invalid time interval");
        }
        return value.isAfter(startDate) && value.isBefore(endDate);
    }
}
