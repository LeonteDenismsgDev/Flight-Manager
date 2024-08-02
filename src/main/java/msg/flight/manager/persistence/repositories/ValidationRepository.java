package msg.flight.manager.persistence.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ValidationRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Document getObject(String  collection, String id){
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Document.class,collection);
    }
}
