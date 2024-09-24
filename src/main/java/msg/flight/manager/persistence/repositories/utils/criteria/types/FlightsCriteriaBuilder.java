package msg.flight.manager.persistence.repositories.utils.criteria.types;

import msg.flight.manager.persistence.repositories.utils.criteria.CriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.SimpleCriteriaBuilder;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.BaseCriteria;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.CompoundCriteria;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.CompoundCriteriaPart;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.SimpleCriteria;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface FlightsCriteriaBuilder extends CriteriaBuilder {
    @CompoundCriteria(compoundCriterias = {
            @CompoundCriteriaPart(simpleCriterias = {
                    @SimpleCriteria(name = "lteStartRecursivePeriodCriteria"),
                    @SimpleCriteria(name = "gtEndRecursivePeriodCriteria")
            }),
            @CompoundCriteriaPart(simpleCriterias = {
                    @SimpleCriteria(name = "gteStartRecursivePeriodCriteria"),
                    @SimpleCriteria(name = "gtEndRecursivePeriodCriteria")
            })
    }
            , operator = "or")
    @SimpleCriteria(name = "isCompanyCriteria")
    @SimpleCriteria(name = "isTypeCriteria")
    @BaseCriteria()
    default Criteria createRecurrenceIntervalCriteria(SimpleCriteriaBuilder gtEndRecursivePeriodCriteria,
                                                      SimpleCriteriaBuilder lteStartRecursivePeriodCriteria,
                                                      SimpleCriteriaBuilder gteStartRecursivePeriodCriteria,
                                                      SimpleCriteriaBuilder isCompanyCriteria,
                                                      SimpleCriteriaBuilder isTypeCriteria) {
        return createDefaultCriteria(isCompanyCriteria, isTypeCriteria);
    }

    @CompoundCriteria(multi = "criteriaFilters")
    default Criteria createFlightIntervalCriteria(List<SimpleCriteriaBuilder> criteriaFilters, SimpleCriteriaBuilder isCompanyCriteria,
                                                  SimpleCriteriaBuilder regexTypeCriteria) {
        return createDefaultCriteria(isCompanyCriteria, regexTypeCriteria);
    }

    @SimpleCriteria(name = "isCompanyCriteria")
    @SimpleCriteria(name = "regexTypeCriteria")
    @SimpleCriteria(name = "inRecursionIdCriteria")
    @BaseCriteria
    default Criteria creteExistentFlightsCriteria(SimpleCriteriaBuilder isCompanyCriteria,
                                                  SimpleCriteriaBuilder regexTypeCriteria,
                                                  SimpleCriteriaBuilder inRecursionIdCriteria) {
        return createDefaultCriteria(isCompanyCriteria, regexTypeCriteria);
    }

    private static Criteria createDefaultCriteria(SimpleCriteriaBuilder companyCriteria, SimpleCriteriaBuilder typeCriteria) {
        Criteria criteria = new Criteria();
        return criteria.andOperator(
                companyCriteria.createCriteria(),
                typeCriteria.createCriteria()
        );
    }
}
