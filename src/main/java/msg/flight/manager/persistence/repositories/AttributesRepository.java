package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.models.flights.DBAttribute;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.utils.ObjectFieldsUtils;
import msg.flight.manager.persistence.repositories.utils.UserRepositoriesUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Attr;

import java.util.List;
import java.util.Map;

@Repository
public class AttributesRepository {
    @Autowired
    private MongoTemplate template;

    public DBAttribute save(DBAttribute attribute) {
        return template.save(attribute);
    }


    public List<AttributeDTO> applicationAttributes(String username) {
        Criteria criteria = new Criteria().orOperator(Criteria.where("createdBy").is(username), Criteria.where("globalVisibility").is(true));
        Query query = new Query(criteria);
        List<AttributeDTO> results = template.find(query, AttributeDTO.class, "attributes");
        List<DBAttribute> comparedResults = template.find(query,DBAttribute.class,"attributes");
        for(int i = 0; i < results.size();i++){
            results.get(i).setEditable(username.equals(comparedResults.get(i).getCreatedBy()));
        }
        return results;
    }

    public boolean delete(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult deleteResult = this.template.remove(query,"attributes");
        return deleteResult.getDeletedCount()>0;
    }

    public boolean update(String id, AttributeDTO updatedAttribute) throws IllegalAccessException {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update();
        Map<String, Object> fields = ObjectFieldsUtils.getFieldsValue(updatedAttribute);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getValue() != null) {
                update.set(entry.getKey(), entry.getValue());
            }
        }
        UpdateResult result = template.updateFirst(query, update, "attributes");
        return result.getModifiedCount() > 0;
    }
}
