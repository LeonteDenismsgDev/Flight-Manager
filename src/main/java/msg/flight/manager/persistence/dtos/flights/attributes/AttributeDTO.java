package msg.flight.manager.persistence.dtos.flights.attributes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeDTO {
    private String id;
    private String label;
    private String name;
    private String required;
    private String type;
    private Object defaultValue;
    private List<String> searchKeyWords;
    private String description;
    private boolean editable;
}
