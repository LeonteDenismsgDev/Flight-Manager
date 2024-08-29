package msg.flight.manager.persistence.dtos.itinerary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryTableResult {
    private int max_itineraries;
    private ArrayList<Itinerary> page;
}
