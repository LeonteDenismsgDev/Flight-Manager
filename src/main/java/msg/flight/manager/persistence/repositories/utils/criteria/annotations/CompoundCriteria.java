package msg.flight.manager.persistence.repositories.utils.criteria.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CompoundCriteriaContainer.class)
public @interface CompoundCriteria {
    SimpleCriteria[] simpleCriterias() default {};
    CompoundCriteriaPart[] compoundCriterias() default {};
    String operator() default "and";
    String multi() default "";
}
