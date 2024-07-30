package msg.flight.manager.persistence.dtos.airport;

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
public class Airport {

    @NotNull(message = "airport icao should not  be null")
    @NotBlank(message = "airport icao should not be blank")
    private String icao;

    @NotNull(message = "airport iata should not  be null")
    @NotBlank(message = "airport iata should not be blank")
    private String iata;

    @NotNull(message = "airport name should not  be null")
    @NotBlank(message = "airport name should not be blank")
    private String airportName;

    @NotNull(message = "airport location should not  be null")
    @NotBlank(message = "airport location should not be blank")
    private String location;

    private Map<String, String> contactData;
}
