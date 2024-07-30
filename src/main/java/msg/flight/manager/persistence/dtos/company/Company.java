package msg.flight.manager.persistence.dtos.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @NotNull(message = "company should not  be null")
    @NotBlank(message = "company should not be blank")
    private String name;
    private int fleet;
    private Map<String, String> contactData;
    private int crews;
}
