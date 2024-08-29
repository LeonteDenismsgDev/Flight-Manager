package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.plane.GetPlane;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.repositories.utils.PlaneRepositoriesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Setter
@Repository
public class PlaneRepository{
    @Autowired
    private MongoTemplate template;

    public boolean save(DBPlane plane){
        if(plane.getRegistrationNumber() == null) return false;
        if(this.get(plane.getRegistrationNumber())!= null) return false;
        this.template.save(plane,"planes");
        return true;
    }

    public List<DBPlane> get(){
        return this.template.findAll(DBPlane.class,"planes");
    }

    public DBPlane get(String registrationNumber){
        Query query = new Query(Criteria.where("_id").is(registrationNumber));
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

    public TableResult getFiltered(PageRequest pageable, GetPlane request){
        AggregationOperation projectConcatFields = Aggregation.project("registrationNumber","manufacturer", "model","manufactureYear","range","cruisingSpeed","wingspan","length","height","company");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("_id")
                        .regex(".*"+request.getFilter()+".*","i"));
        MatchOperation fieldsMatchOperation = PlaneRepositoriesUtils.filterPlaneAggregations(request.getFilter());
        AggregationOperation skip = skip((long) pageable.getPageNumber()*pageable.getPageSize());
        AggregationOperation limit= Aggregation.limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                fieldsMatchOperation,
                projectConcatFields,
                matchSearchString,
                Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult").and(skip,limit).as("paginationResult")
        );
        AggregationResults<TableResult> result = template.aggregate(aggregation,"planes", TableResult.class);
        return result.getMappedResults().get(0);
    }

    public TableResult getFilteredByCompanyAlso(PageRequest pageable, GetPlane request, String company){
        AggregationOperation projectConcatFields = Aggregation.project("registrationNumber","manufacturer", "model","manufactureYear","range","cruisingSpeed","wingspan","length","height","company");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("_id")
                    .regex(".*" + request.getFilter() + ".*","i"));
        MatchOperation matchCompany = Aggregation.match(Criteria.where("company.name").is(company));
        MatchOperation fieldsMatchOperation = PlaneRepositoriesUtils.filterPlaneAggregations(request.getFilter());
        AggregationOperation skip = skip((long) pageable.getPageNumber()*pageable.getPageSize());
        AggregationOperation limit= Aggregation.limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                fieldsMatchOperation,
                projectConcatFields,
                matchSearchString,
                matchCompany,
                Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult").and(skip,limit).as("paginationResult")
        );
        AggregationResults<TableResult> result = template.aggregate(aggregation,"planes", TableResult.class);
        return result.getMappedResults().get(0);
    }

}
