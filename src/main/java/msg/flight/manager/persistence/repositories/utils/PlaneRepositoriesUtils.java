package msg.flight.manager.persistence.repositories.utils;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public class PlaneRepositoriesUtils {
    public static MatchOperation filterPlaneAggregations(String filters){
        Criteria registrationNumberCriteria = Criteria.where("_id").regex(".*"+filters+".*","i");
        return Aggregation.match(registrationNumberCriteria);
    }
}
