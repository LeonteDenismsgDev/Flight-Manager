package msg.flight.manager.persistence.repositories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ValidationRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final Map<String, Map<String, Set<JsonNode>>> validations = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void addValeForPredefinedCollectionCriteria(JsonNode object, String id, String collection) {
        Map<String, Set<JsonNode>> valueList = validations.getOrDefault(collection, new HashMap<>());
        Set<JsonNode> objects = valueList.getOrDefault(id, new HashSet<>());
        objects.add(object);
        valueList.put(id, objects);
        validations.put(collection, valueList);
    }

    @SneakyThrows
    public void checkExistentValues() {
        for (String collection : validations.keySet()) {
            Query query = new Query(Criteria.where("_id").in(validations.get(collection).keySet()));
            List<Document> foundDocuments = mongoTemplate.find(query, Document.class, collection);
            if (foundDocuments.size() != validations.get(collection).keySet().size()) {
                throw new RuntimeException("Invalid due to  non existent " + collection);
            }
        }
    }

    public void cleanValidationValues() {
        validations.clear();
    }


}
