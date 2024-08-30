package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import msg.flight.manager.persistence.dtos.itinerary.GetItineraries;
import msg.flight.manager.persistence.repositories.utils.ItineraryRepositoriesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Setter
@Repository
public class ItineraryRepository {
    @Autowired
    MongoTemplate template;

    public DBItinerary save(DBItinerary dbItinerary){
        return this.template.insert(dbItinerary,"itineraries");
    };

    public List<DBItinerary> get(){
        return this.template.findAll(DBItinerary.class,"itineraries");
    }

    public DBItinerary get(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        DBItinerary it = this.template.findOne(query,DBItinerary.class,"itineraries");
        return it;
    }

    public boolean update(String id, DBItinerary itinerary){
        if(this.get(id) == null) return false;
        DBItinerary old = this.get(id);
        if(!this.delete(id)) return false;
        try {
            return this.save(itinerary) != null;
        }catch(DuplicateKeyException exception) {
            this.save(old);
            throw exception;
        }
    }

    public boolean delete(String id){
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult result = this.template.remove(query, DBItinerary.class,"itineraries");
        return result.getDeletedCount() > 0;
    }

    public TableResult getFiltered(PageRequest pageable, GetItineraries request, String crewID){

        AggregationOperation projectConcatFields = Aggregation.project("id","dep","arr","depTime","arrTime","flightNumber","crewNumber","lateDeparture","lateArrival");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("_id").regex(".*"+request.getFilter()+".*","i"));
        MatchOperation matchCrewID = Aggregation.match(Criteria.where("crewNumber").is(crewID));
        MatchOperation fieldsMatchOperation = ItineraryRepositoriesUtils.filterItineraryAggregations(request.getFilter());
        AggregationOperation skip = skip((long) pageable.getPageNumber()*pageable.getPageSize());
        AggregationOperation limit = Aggregation.limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                fieldsMatchOperation,
                projectConcatFields,
                matchSearchString,
                matchCrewID,
                Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult")
                        .and(skip,limit).as("paginationResult")
        );
        AggregationResults<TableResult> results = template.aggregate(aggregation,"itineraries", TableResult.class);
        return results.getMappedResults().get(0);
    }
}
