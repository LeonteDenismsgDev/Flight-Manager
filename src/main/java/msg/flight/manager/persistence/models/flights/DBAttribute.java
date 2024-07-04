package msg.flight.manager.persistence.models.flights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "attributes")
public class DBAttribute {
    @Id
    private String id;
    private String name;
    private String label;
    private boolean required;
    private String type;
    private Object defaultValue;
    private String createdBy;
    private boolean globalVisibility;
    private List<String> searchKeyWords;
    private String description;
}