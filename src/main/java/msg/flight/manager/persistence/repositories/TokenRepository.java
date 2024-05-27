package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.UpdateResult;
import msg.flight.manager.persistence.models.user.DBToken;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class TokenRepository {

    @Autowired
    MongoTemplate template;

    public Boolean isTokenRejected(String token) {
        Query query = new Query(Criteria.where("token").is(token));
        DBToken result = template.findOne(query, DBToken.class);
        return result != null ? result.getRejected() : null;
    }

    public long rejectToken(String token) {
        Query query = new Query(Criteria.where("token").is(token));
        Update update = new Update().set("rejected", true);
        UpdateResult result = template.updateFirst(query, update, DBToken.class);
        return result.getModifiedCount();
    }

    public void save(DBToken token) {
        template.save(token,"tokens");
    }

    public void manageTokens(){
        Criteria criteria = new Criteria().orOperator(Criteria.where("rejected").is(true),Criteria.where("expirationDate").lt(LocalDateTime.now()));
        Query query = new Query(criteria);
        template.remove(query,"tokens");
    }


}
