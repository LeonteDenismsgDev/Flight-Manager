package msg.flight.manager.persistence.dtos.flights.attributes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAttribute {
    @NotNull(message = "The name of the attribute should not be null")
    @NotBlank(message = "The name of the attribute should not be empty")
    private String name;
    @NotNull(message = "The type of the attribute should not be null")
    @NotBlank(message = "The type of the attribute should not be empty")
    private String type;
    @NotNull(message = "The description of the attribute should not be null")
    @NotBlank(message = "The description of the attribute should not be empty")
    private String description;
    private Object defaultValue;
    @NotNull(message = "The attribute visibility is not mentioned")
    private boolean isGlobal;
    @NotNull(message = "The attribute require status is not mentioned")
    private boolean isRequired;
}
