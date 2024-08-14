package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Setter
@Repository
public class ItineraryRepository {
    @Autowired
    MongoTemplate template;

    public DBItinerary save(DBItinerary dbItinerary){
        return this.template.insert(dbItinerary,"itineraries");
    };

    public List<DBItinerary> get(){
        return this.template.findAll(DBItinerary.class,"itineraries");
    }

    public DBItinerary get(long id){
        Query query = new Query(Criteria.where("_id").is(id));
        return this.template.findOne(query,DBItinerary.class,"itineraries");
    }

    public boolean update(long id, DBItinerary itinerary){
        if(this.get(id) == null) return false;
        DBItinerary old = this.get(id);
        if(!this.delete(id)) return false;
        try {
            return this.save(itinerary) != null;
        }catch(DuplicateKeyException exception) {
            this.save(old);
            throw exception;
        }
    }

    public boolean delete(long id){
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult result = this.template.remove(query, DBItinerary.class,"itineraries");
        return result.getDeletedCount() > 0;
    }
}
