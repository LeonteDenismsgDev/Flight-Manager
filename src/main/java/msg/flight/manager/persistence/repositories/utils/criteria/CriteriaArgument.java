package msg.flight.manager.persistence.repositories.utils.criteria;

import java.util.List;
import java.util.stream.Collectors;


public class CriteriaArgument {
    private SimpleCriteriaBuilder criteriaBuilder;
    private List<SimpleCriteriaBuilder> criteriaBuilders;

    public CriteriaArgument(Object criteria) {
        if (criteria instanceof SimpleCriteriaBuilder) {
            this.criteriaBuilder = (SimpleCriteriaBuilder) criteria;
        } else {
            if (criteria instanceof List<?> tempList) {
                if (tempList.stream().allMatch(item -> item instanceof SimpleCriteriaBuilder)) {
                    this.criteriaBuilders = tempList.stream().map(item -> (SimpleCriteriaBuilder) item).collect(Collectors.toList());
                } else {
                    throw new IllegalArgumentException("List contains elements that are not of type SimpleCriteriaBuilder");
                }
            }
        }
    }

    public SimpleCriteriaBuilder getSimpleCriteriaArgument(){
        if(criteriaBuilder == null){
            throw new RuntimeException("Invalid argument type");
        }
        return criteriaBuilder;
    }

    public List<SimpleCriteriaBuilder> getArrayCriteriaArgument(){
        if(criteriaBuilders == null){
            throw new RuntimeException("Invalid argument exception");
        }
        return criteriaBuilders;
    }
}
