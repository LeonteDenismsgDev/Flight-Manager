package msg.flight.manager.persistence.repositories.utils;

import msg.flight.manager.persistence.dtos.airport.GetAirport;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public class AirportRepositoriesUtils {
    public static MatchOperation filterAirportAggregations(String filters){
        Criteria criteria = new Criteria();
        Criteria icaoCriteria = Criteria.where("_id").regex(".*"+filters+".*","i");
        Criteria iataCriteria = Criteria.where("iata").regex(".*"+filters+".*","i");
        Criteria nameCriteria = Criteria.where("airportName").regex(".*"+filters+".*","i");
        Criteria locationCriteria = Criteria.where("location").regex(".*"+filters+".*","i");
        criteria.orOperator(icaoCriteria,iataCriteria,nameCriteria,locationCriteria);
        return Aggregation.match(criteria);
    }
}
