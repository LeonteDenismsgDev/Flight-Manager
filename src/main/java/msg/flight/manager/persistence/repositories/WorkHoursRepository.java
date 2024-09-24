package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.user.DBUser;
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
        Criteria firstCriteria = new Criteria().andOperator(
                Criteria.where("endTime").gte(startTime),
                Criteria.where("startTime").lte(startTime)
        );
        Criteria secondCriteria = new Criteria().andOperator(
                Criteria.where("startTime").gte(startTime),
                Criteria.where("startTime").lte(endTime)
        );
        Criteria criteria = new Criteria();
        criteria.orOperator(
                firstCriteria,
                secondCriteria
        );
        Query overlappingEntriesQuery = new Query(criteria);
        List<DBUserWorkHours> overlappingEntries = template.find(overlappingEntriesQuery, DBUserWorkHours.class);
        List<String> overlappingUsernames = overlappingEntries.stream()
                .map(DBUserWorkHours::getUser)
                .distinct()
                .collect(Collectors.toList());
        Criteria usersCriteria = new Criteria();
        usersCriteria.andOperator(
                Criteria.where("_id").nin(overlappingUsernames),
                Criteria.where("role").is("CREW_ROLE")
        );
        List<DBUser> users = template.find(new Query(usersCriteria), DBUser.class,"users");
        return users.stream().map(DBUser::getUsername).distinct().toList();
    }
}
