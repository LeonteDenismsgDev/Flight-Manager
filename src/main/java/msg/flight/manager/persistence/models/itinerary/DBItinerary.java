package msg.flight.manager.persistence.models.itinerary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.plane.Plane;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "itineraries")
public class DBItinerary {

    @Id
    private String id;
    private String dep;
    private String arr;
    //Times are stored in milliseconds
    private long depTime;
    private long arrTime;
    private Plane plane;

    @Override
    public boolean equals(Object o){
        if(o == null || getClass() != o.getClass()) return false;
        if(this == o) return true;
        DBItinerary that = (DBItinerary) o;
        return dep.equals(that.getDep()) &&
                arr.equals(that.getArr()) &&
                depTime == that.getDepTime() &&
                arrTime == that.getArrTime() &&
                plane == that.getPlane();
    }

    @Override
    public int hashCode(){
        return Objects.hash(dep,arr,depTime,arrTime,plane);
    }
}
