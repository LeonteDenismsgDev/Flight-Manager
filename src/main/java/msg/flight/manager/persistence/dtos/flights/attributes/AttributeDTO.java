package msg.flight.manager.persistence.dtos.flights.attributes;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class AttributeDTO {
    private String id;
    private String label;
    private String name;
    private boolean required;
    private boolean globalVisibility;
    private String type;
    private Object defaultValue;
    private List<String> searchKeyWords;
    private String description;
    private boolean editable;
}
