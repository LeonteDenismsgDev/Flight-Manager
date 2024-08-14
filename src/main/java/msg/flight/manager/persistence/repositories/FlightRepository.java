package msg.flight.manager.persistence.repositories;

import com.mongodb.KerberosSubjectProvider;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.objenesis.ObjenesisHelper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class FlightRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> save(Map<String, Object> flight){
        return mongoTemplate.save(flight,"flights");
    }

    public List<Document> getFlights(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Query query = new Query(Criteria.where("type").is("attribute")).with(pageable);
        return  mongoTemplate.find(query,Document.class,"flights");
    }

    public long deleteFlight(String flightId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("type").is("attribute"),
                Criteria.where("_id").is(flightId)
        );
        Query query = new Query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query,"flights");
        return  deleteResult.getDeletedCount();
    }

    public Document findFlightById(String flightId) {
        Query query = new Query(Criteria.where("_id").is(flightId));
        return mongoTemplate.findOne(query, Document.class,"flights");
    }

    public void updateFlight(Map<String, Object> flightDoc, String flightId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("_id").is(flightId),Criteria.where("type").is("attribute"));
        Query query = new Query(Criteria.where("_id").is(flightId));
        mongoTemplate.findAndReplace(query,flightDoc,"flights");
    }
}
