package msg.flight.manager.persistence.dtos.flights.templates;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeleteTemplate {
    @NotBlank(message = "The template name should not be empty")
    @NotNull(message = "The template  name should not be null")
    String name;
}
