package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.flights.DBMessageNotification;
import msg.flight.manager.persistence.models.flights.DBRecordNotification;
import msg.flight.manager.persistence.repositories.utils.criteria.SimpleCriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.types.NotificationCriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository {
    @Autowired
    private NotificationCriteriaBuilder notificationCriteriaBuilder;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<DBRecordNotification> getFlightNotifications(String recurrenceId) {
        SimpleCriteriaBuilder isRecurrenceCriteria = new SimpleCriteriaBuilder("recurrenceId","is",recurrenceId);
        SimpleCriteriaBuilder isTypeCriteria = new SimpleCriteriaBuilder("type","is","record");
        Criteria criteria =  notificationCriteriaBuilder.getRecurrenceNotification(isRecurrenceCriteria,isTypeCriteria);
        Query query = new Query(criteria);
        return mongoTemplate.find(query,DBRecordNotification.class,"flightNotifications");
    }

    public void insertRecordNotifications(List<DBRecordNotification> recordNotifications) {
        mongoTemplate.insertAll(recordNotifications);
    }

    public void saveMessageNotification(DBMessageNotification messageNotification){
        mongoTemplate.save(messageNotification,"flightNotifications");
    }
}
