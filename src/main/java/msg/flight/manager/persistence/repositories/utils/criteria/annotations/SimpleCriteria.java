package msg.flight.manager.persistence.repositories.utils.criteria.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = SimpleCriteriaContainer.class)
public @interface SimpleCriteria {
     String name();
}
