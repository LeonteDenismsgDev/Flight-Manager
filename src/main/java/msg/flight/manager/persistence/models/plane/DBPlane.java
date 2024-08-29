package msg.flight.manager.persistence.models.plane;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.company.Company;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "planes")
public class DBPlane {

    @Id
    private String registrationNumber;
    private String model;
    private String manufacturer;
    private int manufactureYear;
    private int range;
    private int cruisingSpeed;
    private float wingspan;
    private float length;
    private float height;
    private Company company;
}