package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.models.airport.DBAirport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Setter
public class AirportRepository {

    @Autowired
    MongoTemplate template;

    public DBAirport get(String icao){
        Query query = new Query(Criteria.where("_id").is(icao));
        return template.findOne(query,DBAirport.class,"airports");
    }

    public List<DBAirport> get(){
        return template.findAll(DBAirport.class,"airports");
    }

    public Boolean save(DBAirport airport){
        if(get(airport.getIcao()) != null) return false;
        template.save(airport,"airports");
        return true;
    }

    public Boolean delete(String icao){
        Query query = new Query(Criteria.where("_id").is(icao));
        DeleteResult result = template.remove(query,"airports");
        return result.getDeletedCount()>0;
    }

    public Boolean update(String icao,DBAirport airport){
        if(!delete(icao)) return false;
        return save(airport);
    }
}
