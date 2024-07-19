package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.plane.DBPlane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaneRepository {
    @Autowired
    MongoTemplate template;

    public void save(DBPlane plane){
        this.template.save(plane,"planes");
    }

    public List<DBPlane> get(){
        return this.template.findAll(DBPlane.class,"planes");
    }

    public DBPlane get(String registrationNumber){
        Query query = new Query(Criteria.where("registrationNumber").is(registrationNumber));
        return this.template.findOne(query,DBPlane.class,"planes");
    }

    public boolean delete(String registrationNumber){
        Query query = new Query(Criteria.where("registrationNumber").is(registrationNumber));
        DeleteResult result = this.template.remove(query,"planes");
        return result.getDeletedCount()>0;
    }

    public void update(String registrationNumber, DBPlane plane){
        if(!delete(registrationNumber)) throw new RuntimeException("Unable to update the plane");
        save(plane);
    }
}
