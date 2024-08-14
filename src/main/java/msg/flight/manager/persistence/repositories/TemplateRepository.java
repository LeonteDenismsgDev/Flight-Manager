package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.flights.templates.TemplateDTO;
import msg.flight.manager.persistence.models.flights.DBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TemplateRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public DBTemplate saveTemplate(DBTemplate template) {
        Query query = new Query(Criteria.where("_id").is(template.getName()));
        DBTemplate existingTemplate = mongoTemplate.findOne(query, DBTemplate.class);
        if (existingTemplate != null) {
            return null;
        }
        return mongoTemplate.save(template);
    }

    public TableResult findTemplates(int page, int size) {
        FacetOperation operation = Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult")
                .and(Aggregation.skip((long) page * size), Aggregation.limit(size)).as("paginationResult");
        Aggregation aggregation = Aggregation.newAggregation(operation);
        AggregationResults<TableResult> result = mongoTemplate.aggregate(aggregation, "templates", TableResult.class);
        try {
            return result.getMappedResults().get(0);
        }catch (Exception ex){
            return null;
        }
    }

    public long deleteTemplate(String name){
        Query query = new Query(Criteria.where("_id").is(name));
        DeleteResult result = this.mongoTemplate.remove(query, "templates");
        return result.getDeletedCount();
    }

    public int updateTemplate(String name, DBTemplate updatedTemplate) throws IllegalAccessException {
        long deleted = this.deleteTemplate(name);
        if(deleted <= 0){
            return -1;
        }
        DBTemplate savedTemplate = this.saveTemplate(updatedTemplate);
        if(savedTemplate != updatedTemplate){
            return -2;
        }
        return 0;
    }

    public TemplateDTO findTemplate(String name) {
        Query query  = new Query(Criteria.where("_id").is(name));
       return this.mongoTemplate.findOne(query,TemplateDTO.class,"templates");
    }
}
