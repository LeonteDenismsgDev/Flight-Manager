package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.UpdateResult;
import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.CrewUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.utils.UserRepositoriesUtils;
import msg.flight.manager.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class UserRepository {
    @Autowired
    MongoTemplate template;

    public Optional<SecurityUser> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        query.fields().include("username").include("role").include("password").include("enabled").include("company");
        SecurityUser result = template.findOne(query, SecurityUser.class, "users");
        return Optional.ofNullable(result);
    }

    public CrewUpdateUser findDataByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return template.findOne(query, CrewUpdateUser.class, "users");
    }

    public long updateUser(UpdateUserDto userDto) throws IllegalAccessException {
        Query query = new Query(Criteria.where("username").is(userDto.getUsername()));
        Update update = new Update();
        Map<String, Object> fields = UserRepositoriesUtils.getFieldsValue(userDto);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getValue() != null) {
                update.set(entry.getKey(), entry.getValue());
            }
        }
        UpdateResult result = template.updateFirst(query, update, DBUser.class);
        return result.getModifiedCount();
    }

    public DBUser save(DBUser user) {
        return template.save(user, "users");

    }


    public long updatePassword(String encodedPassword, String username) {
        Query query = new Query(Criteria.where("username").is(username));
        Update update = new Update();
        update.set("password", encodedPassword);
        UpdateResult result = template.updateFirst(query, update, DBUser.class);
        return result.getModifiedCount();
    }

    public String toggleEnable(String username) {
        Query query = new Query(Criteria.where("_id").is(username));
        DBUser user = template.findOne(query, DBUser.class);
        if (user != null) {
            Update update = new Update();
            update.set("enabled", !user.getEnabled());
            template.updateFirst(query, update, DBUser.class);
            return user.getContactData().get("email");
        }
        return null;
    }

    public void deleteUser(String username) {
        Query query = new Query(Criteria.where("_id").is(username));
        template.remove(query, "users");
    }

    public List<UpdateUserDto> filterUsers(PageRequest pageable, UsersFilterOptions filters, String role, String company) {
        AggregationOperation projectConcatFields = Aggregation.project("firstName", "lastName", "contactData", "address", "company", "role")
                .andExpression("concat(firstName, ' ', lastName)").as("fullName");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("fullName").regex(".*" + filters.getFullName() + ".*", "i"));
        MatchOperation filedsMatchOperation = UserRepositoriesUtils.filterUserAggregation(filters, role, company);
        AggregationOperation skip = skip((long) pageable.getPageNumber() * pageable.getPageSize());
        AggregationOperation limit = limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                filedsMatchOperation,
                projectConcatFields,
                matchSearchString,
                skip,
                limit
        );
        if (role.equals(Role.ADMINISTRATOR_ROLE.name())) {
            AggregationResults<AdminUpdateUser> users = template.aggregate(aggregation, "users", AdminUpdateUser.class);
            return users.getMappedResults().stream().map(user -> (UpdateUserDto) user).collect(Collectors.toList());
        }
        AggregationResults<CrewUpdateUser> users = template.aggregate(aggregation, "users", CrewUpdateUser.class);
        return users.getMappedResults().stream().map(user -> (UpdateUserDto) user).collect(Collectors.toList());
    }
}
