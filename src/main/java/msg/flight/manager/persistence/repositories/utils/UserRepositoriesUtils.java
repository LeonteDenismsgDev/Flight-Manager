package msg.flight.manager.persistence.repositories.utils;

import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.enums.Role;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoriesUtils {
    public static Map<String, Object> getFieldsValue(UpdateUserDto userDto) throws IllegalAccessException {
        Map<String, Object> fieldsValue = new HashMap<>();
        Class<?> clazz = userDto.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                fieldsValue.put(field.getName(), field.get(userDto));
            }
            clazz = clazz.getSuperclass();
        }
        return fieldsValue;
    }

    public static MatchOperation filterUserAggregation(UsersFilterOptions options, String role, String company) {
        Criteria criteria = new Criteria();
        Criteria companyCriteria = Criteria.where("company").regex(options.getCompany());
        Criteria roleCriteria = Criteria.where("role").in(options.getRoles());
        if (options.getRoles().isEmpty()) {
            roleCriteria = Criteria.where("role").regex("^[A-Z]+(_[A-Z]+)*_ROLE$");
        }
        Criteria accessCriteria = Criteria.where("canBeViewBy").in(role);
        criteria.andOperator(companyCriteria, roleCriteria, accessCriteria);
        if (role.equals(Role.COMPANY_MANAGER_ROLE.name())) {
            return Aggregation.match(new Criteria().andOperator(criteria, Criteria.where("company").is(company)));
        }
        return Aggregation.match(criteria);
    }
}
