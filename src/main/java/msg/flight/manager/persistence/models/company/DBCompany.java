package msg.flight.manager.persistence.models.company;

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
@Document(collection = "companies")
public class DBCompany {
    @Id
    private String name;
    private int fleet;
    private Map<String,String> contactData;
}
