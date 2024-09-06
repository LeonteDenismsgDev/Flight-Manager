package msg.flight.manager.persistence.dtos.plane;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.company.Company;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaneDataTableView {
    @NotNull(message = "plane model should not be null")
    @NotBlank(message = "plane model should not be blank")
    private String model;

    @NotNull(message = "plane registration number should not be null")
    @NotBlank(message = "plane registration number should not be blank")
    private String registrationNumber;

    @NotNull(message = "plane manufacturer should not be null")
    @NotBlank(message = "plane manufacturer should not be blank")
    private String manufacturer;

    @NotNull(message = "plane manufacture year should not be null")
    @NotBlank(message = "plane manufacture year should not be blank")
    private Integer manufactureYear;

    @NotNull(message = "plane year should not  be null")
    @NotBlank(message = "plane year should not be blank")
    private Integer range;

    @NotNull(message = "plane cruising speed should not be null")
    @NotBlank(message = "plane cruising speed should not be blank")
    private Integer cruisingSpeed;

    @NotNull(message = "plane wingspan should not be null")
    @NotBlank(message = "plane wingspan should not be blank")
    private Double wingspan;

    @NotNull(message = "plane length should not be null")
    @NotBlank(message = "plane length should not be blank")
    private Double length;

    @NotNull(message = "plane height should not be null")
    @NotBlank(message = "plane height should not be blank")
    private Double height;

    @NotNull(message = "plane company should not be null")
    @NotBlank(message = "plane company should not be blank")
    private Company company;
}