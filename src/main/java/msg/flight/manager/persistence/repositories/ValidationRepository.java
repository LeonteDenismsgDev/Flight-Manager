package msg.flight.manager.persistence.repositories;

import org.bson.BsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ValidationRepository {

    @Autowired
    private MongoTemplate template;

    public BsonDocument getDocument(String collection, String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return template.findOne(query, BsonDocument.class, collection);
    }
}
