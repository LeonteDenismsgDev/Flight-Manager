package msg.flight.manager.persistence.dtos.flights.flights;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GetDateFlightsDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime start;
    private LocalTime end;

    public LocalDate getStartDate() {
        if (this.startDate == null) {
            return LocalDate.now();
        }
        return this.startDate;
    }

    public LocalDate getEndDate() {
        if (this.endDate == null) {
            return LocalDate.now();
        }
        return this.endDate;
    }

    public LocalTime getStart() {
        if (this.start == null) {
            return LocalTime.MIN;
        }
        return this.start;
    }

    public LocalTime getEnd() {
        if (this.end == null) {
            return LocalTime.MAX;
        }
        return this.end;
    }

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(this.getStartDate(), this.getStart());
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(this.getEndDate(), this.getEnd());
    }
}
