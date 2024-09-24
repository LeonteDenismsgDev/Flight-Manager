package msg.flight.manager.persistence.repositories.utils.criteria;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;

@Getter
@Setter
public class SimpleCriteriaBuilder {
    private String operand;
    private String operator;
    private Object value;

    @Builder
    public SimpleCriteriaBuilder(@NotNull String operand, @NotNull String operator, @NotNull Object value) {
        this.operand = operand;
        this.operator = operator;
        this.value = value;
    }

    public Criteria createCriteria() {
        switch (operator) {
            case "is":
                return Criteria.where(operand).is(value);
            case "regex":
                if (value instanceof String) {
                    return Criteria.where(operand).regex(value.toString());
                }
                break;
            case "lt":
                return Criteria.where(operand).lt(value);
            case "lte":
                return Criteria.where(operand).lte(value);
            case "gt":
                return Criteria.where(operand).gt(value);
            case "gte":
                return Criteria.where(operand).gte(value);
            case "exists":
                if(value instanceof Boolean){
                    return Criteria.where(operand).exists((Boolean)value);
                }
                break;
            case "in":
                if (value instanceof Collection<?>) {
                    return Criteria.where(operand).in(value);
                }
                break;
            case "nin":
                if (value instanceof Collection<?>) {
                    return Criteria.where(operand).nin(value);
                }
                break;
        }
        return new Criteria();
    }
}
