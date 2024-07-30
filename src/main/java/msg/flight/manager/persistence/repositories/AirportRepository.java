package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.Setter;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.airport.Airport;
import msg.flight.manager.persistence.dtos.airport.AirportTableResult;
import msg.flight.manager.persistence.dtos.airport.GetAirport;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.repositories.utils.AirportRepositoriesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

@Repository
@Setter
public class AirportRepository {

    @Autowired
    private MongoTemplate template;

    public DBAirport get(String icao){
        Query query = new Query(Criteria.where("_id").is(icao));
        return template.findOne(query,DBAirport.class,"airports");
    }

    public List<DBAirport> get(){
        return template.findAll(DBAirport.class,"airports");
    }

    public Boolean save(DBAirport airport){
        if(get(airport.getIcao()) != null) return false;
        template.save(airport,"airports");
        return true;
    }

    public Boolean delete(String icao){
        Query query = new Query(Criteria.where("_id").is(icao));
        DeleteResult result = template.remove(query,"airports");
        return result.getDeletedCount()>0;
    }

    public Boolean update(String icao,DBAirport airport){
        if(!delete(icao)) return false;
        return save(airport);
    }
    private DBAirport generateAirport(String icao,String iata,String name, String location, Map<String,String> contactData){
        return DBAirport
                .builder()
                .icao(icao)
                .iata(iata)
                .airportName(name)
                .location(location)
                .contactData(contactData)
                .build();
    }
    public void prepare(){

        this.template.save(generateAirport("LIMC","MXP","Milan Malpensa Airport","Milan",new HashMap<>()));
        this.template.save(generateAirport("LIPZ","VCE","Venice Marco Polo Airport","Venice",new HashMap<>()));
        this.template.save(generateAirport("LIRN","NAP","Naples International Airport","Naples",new HashMap<>()));
        this.template.save(generateAirport("LIMJ","GOA","Genoa Cristoforo Colombo Airport","Genoa",new HashMap<>()));
        this.template.save(generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>()));
    }

    public TableResult getFilteredList(PageRequest pageable, GetAirport request){
        AggregationOperation projectConcatFields = Aggregation.project("icao","iata","airportName","location");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("_id")
                        .regex(".*"+request.getFilter()+".*","i"));
        MatchOperation fieldsMatchOperation = AirportRepositoriesUtils.filterAirportAggregations(request.getFilter());
        AggregationOperation skip = skip((long) pageable.getPageNumber()*pageable.getPageSize());
        AggregationOperation limit = Aggregation.limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                fieldsMatchOperation,
                projectConcatFields,
                matchSearchString,
                Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult")
                        .and(skip,limit).as("paginationResult")
        );
        AggregationResults<TableResult> result = template.aggregate(aggregation,"airports", TableResult.class);
        return result.getMappedResults().get(0);
    }
}
