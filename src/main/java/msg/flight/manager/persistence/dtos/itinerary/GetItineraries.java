package msg.flight.manager.persistence.dtos.itinerary;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetItineraries {
    private int size;
    private int page;
    private String filter;
}
