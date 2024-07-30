package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.plane.DBPlane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Setter
@Repository
public class PlaneRepository{
    @Autowired
    private MongoTemplate template;

    public boolean save(DBPlane plane){
        if(this.get(plane.getRegistrationNumber())!= null) return false;
        this.template.save(plane,"planes");
        return true;
    }

    public List<DBPlane> get(){
        return this.template.findAll(DBPlane.class,"planes");
    }

    public DBPlane get(String registrationNumber){
        Query query = new Query(Criteria.where("registrationNumber").is(registrationNumber));
        return this.template.findOne(query,DBPlane.class,"planes");
    }

    public boolean delete(String registrationNumber){
        Query query = new Query(Criteria.where("_id").is(registrationNumber));
        DeleteResult result = this.template.remove(query,"planes");
        return result.getDeletedCount()>0;
    }

    public void update(String registrationNumber, DBPlane plane){
        if(!delete(registrationNumber)) throw new RuntimeException("Unable to update the plane");
        save(plane);
    }

}
