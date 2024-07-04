package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.models.flights.DBAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        return template.find(query, AttributeDTO.class, "attributes");
    }

}
