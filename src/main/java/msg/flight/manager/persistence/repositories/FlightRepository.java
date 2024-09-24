package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.DeleteResult;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import msg.flight.manager.persistence.mappers.DocumentMapper;
import msg.flight.manager.persistence.models.flights.DBFlight;
import msg.flight.manager.persistence.models.flights.DBRecurrence;
import msg.flight.manager.persistence.repositories.utils.criteria.SimpleCriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.types.FlightsCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public class FlightRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FlightsCriteriaBuilder criteriaBuilder;
    @Autowired
    private DocumentMapper documentMapper;

    @Transactional
    public List<DBFlight> insertMultipleFlights(List<DBFlight> flights) {
        List<DBFlight> savedFlights = (List<DBFlight>) mongoTemplate.insert(flights, "flights");
        List<DBFlight> auditFlights = flights.stream().map(DBFlight::getAuditEntry).toList();
        mongoTemplate.insert(auditFlights, "flights");
        return savedFlights;
    }

    public DBFlight save(@NonNull DBFlight flight) {
        return mongoTemplate.save(flight, "flights");
    }

    public DBRecurrence save(@NotNull DBRecurrence flightRecurrence) {
        return mongoTemplate.save(flightRecurrence, "flights");
    }

    public List<DBFlight> getDBFlights(@NotNull String company, @NotNull LocalDateTime startTimeInterval, @NotNull LocalDateTime endTimeInterval) {
        SimpleCriteriaBuilder isCompanyCriteria = new SimpleCriteriaBuilder("company", "is", company);
        SimpleCriteriaBuilder regexTypeCriteria = new SimpleCriteriaBuilder("type", "regex", "flight");
        SimpleCriteriaBuilder existsRecurrenceIdCriteria = new SimpleCriteriaBuilder("recursionId", "exists", false);
        SimpleCriteriaBuilder lteDepartureTimeCriteria = new SimpleCriteriaBuilder("departureTime", "lte", endTimeInterval);
        SimpleCriteriaBuilder gteDepartureTime = new SimpleCriteriaBuilder("departureTime", "gte", startTimeInterval);
        Query query = new Query(criteriaBuilder.createFlightIntervalCriteria(List.of(existsRecurrenceIdCriteria,
                lteDepartureTimeCriteria, gteDepartureTime), isCompanyCriteria, regexTypeCriteria));
        return mongoTemplate.find(query, DBFlight.class, "flights");
    }

    public List<DBFlight> getDBFlights(@NotNull String company, @NotNull @NotEmpty Set<String> recurrenceIds) {
        SimpleCriteriaBuilder isCompanyCriteria = new SimpleCriteriaBuilder("company", "is", company);
        SimpleCriteriaBuilder regexTypeCriteria = new SimpleCriteriaBuilder("type", "regex", "flight");
        SimpleCriteriaBuilder inRecursionIdCriteria = new SimpleCriteriaBuilder("recursionId", "in", recurrenceIds);
        Query query = new Query(criteriaBuilder.creteExistentFlightsCriteria(isCompanyCriteria, regexTypeCriteria, inRecursionIdCriteria));
        return mongoTemplate.find(query, DBFlight.class, "flights");
    }

    public List<DBRecurrence> getDBRecurrences(@NotNull String company, @NotNull LocalDateTime startTimeInterval) {
        SimpleCriteriaBuilder gtEndRecursivePeriodCriteria = new SimpleCriteriaBuilder("endRecursivePeriod", "gt", startTimeInterval);
        SimpleCriteriaBuilder lteStartRecursivePeriodCriteria = new SimpleCriteriaBuilder("startRecursivePeriod", "lte", startTimeInterval);
        SimpleCriteriaBuilder gteStartRecursivePeriodCriteria = new SimpleCriteriaBuilder("startRecursivePeriod", "gte", startTimeInterval);
        SimpleCriteriaBuilder isCompanyCriteria = new SimpleCriteriaBuilder("company", "is", company);
        SimpleCriteriaBuilder isTypeCriteria = new SimpleCriteriaBuilder("type", "is", "recurrence");
        Query query = new Query(criteriaBuilder.createRecurrenceIntervalCriteria(gtEndRecursivePeriodCriteria,
                lteStartRecursivePeriodCriteria, gteStartRecursivePeriodCriteria, isCompanyCriteria, isTypeCriteria));
        return mongoTemplate.find(query, DBRecurrence.class, "flights");
    }

    public long deleteFlight(String flightId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("type").is("flight"),
                Criteria.where("_id").is(flightId)
        );
        Query query = new Query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, "flights");
        return deleteResult.getDeletedCount();
    }

    public DBFlight findFlightById(String flightId) {
        Query query = new Query(Criteria.where("_id").is(flightId));
        return mongoTemplate.findOne(query, DBFlight.class, "flights");
    }

    public DBRecurrence findRecurrenceById(String recurrenceId){
        Query query = new Query(Criteria.where("_id").is(recurrenceId));
        return mongoTemplate.findOne(query, DBRecurrence.class,"flights");
    }

    public long deleteRecurrence(String recurrenceId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("type").is("recurrence"),
                Criteria.where("_id").is(recurrenceId)
        );
        Query query = new Query(criteria);
        DeleteResult deleteResult = mongoTemplate.remove(query, "flights");
        return deleteResult.getDeletedCount();
    }
}
