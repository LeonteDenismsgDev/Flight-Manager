package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.user.DBUserWorkHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class WorkHoursRepository {
    @Autowired
    private MongoTemplate template;

    public List<String> findAvailableUsers(LocalDateTime startTime, LocalDateTime endTime, String startLocation) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("endTime").lt(startTime),
                Criteria.where("startTime").gt(endTime)
        );
        Query overlappingEntriesQuery = new Query(new Criteria().norOperator(criteria));
        List<DBUserWorkHours> overlappingEntries = template.find(overlappingEntriesQuery, DBUserWorkHours.class);
        List<String> overlappingUsernames = overlappingEntries.stream()
                .map(DBUserWorkHours::getUser)
                .distinct()
                .collect(Collectors.toList());
        Criteria queryCriteria = new Criteria().andOperator(
                Criteria.where("user").nin(overlappingUsernames),
                Criteria.where("endTime").lt(startTime)
        );
        Query lastFlights = new Query(queryCriteria).with(Sort.by(Sort.Direction.DESC, "endTime")).limit(1);
        List<DBUserWorkHours> flights = template.find(lastFlights, DBUserWorkHours.class);
        return flights.stream().filter(hour -> hour.getLastLocation().equals(startLocation)).map(DBUserWorkHours::getUser).distinct().toList();
    }
}
