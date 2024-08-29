package msg.flight.manager.persistence.repositories.utils;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ItineraryRepositoriesUtils {
    public static MatchOperation filterItineraryAggregations(String filters){
        Criteria idCriteria = Criteria.where("_id").regex(".*"+filters+".*","i");
        return Aggregation.match(idCriteria);
    }
}
