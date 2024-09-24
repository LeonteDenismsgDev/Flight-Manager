package msg.flight.manager.persistence.repositories.utils.criteria.types;

import msg.flight.manager.persistence.repositories.utils.criteria.CriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.SimpleCriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.BaseCriteria;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.SimpleCriteria;
import org.springframework.data.mongodb.core.query.Criteria;

public interface NotificationCriteriaBuilder extends CriteriaBuilder {

    @SimpleCriteria(name = "isRecurrenceCriteria")
    @SimpleCriteria(name = "isTypeCriteria")
    @BaseCriteria
    default Criteria getRecurrenceNotification(SimpleCriteriaBuilder isRecurrenceCriteria,SimpleCriteriaBuilder isTypeCriteria){
      return Criteria.where("type").is("nonExistentType");
    }
}
