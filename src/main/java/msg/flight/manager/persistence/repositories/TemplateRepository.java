package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.dtos.TableResult;
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
        return result.getMappedResults().get(0);
    }
}
