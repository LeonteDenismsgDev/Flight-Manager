package msg.flight.manager.persistence.models.itinerary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.plane.Plane;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Time;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "itineraries")
public class DBItinerary {

    @Id
    private long id;
    private String dep;
    private String arr;
    private Date depTime;
    private Date arrTime;
    private Plane plane;
}
