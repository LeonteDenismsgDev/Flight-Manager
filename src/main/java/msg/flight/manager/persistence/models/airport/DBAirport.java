package msg.flight.manager.persistence.models.airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "airports")
public class DBAirport {
    @Id
    private String icao;
    private String iata;
    private String airportName;
    private String location;
    private Map<String,String> contactData;
}
