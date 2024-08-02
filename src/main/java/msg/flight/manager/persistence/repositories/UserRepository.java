package msg.flight.manager.persistence.repositories;

import com.mongodb.client.result.UpdateResult;
import msg.flight.manager.persistence.dtos.TableResult;
import msg.flight.manager.persistence.dtos.user.UsersFilterOptions;
import msg.flight.manager.persistence.dtos.user.update.AdminUpdateUser;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.utils.ObjectFieldsUtils;
import msg.flight.manager.persistence.repositories.utils.UserRepositoriesUtils;
import msg.flight.manager.security.SecurityUser;
import org.slf4j.event.KeyValuePair;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class UserRepository {
    @Autowired
    private MongoTemplate template;

    public Optional<SecurityUser> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        query.fields().include("username").include("role").include("password").include("enabled").include("company");
        SecurityUser result = template.findOne(query, SecurityUser.class, "users");
        return Optional.ofNullable(result);
    }

    public AdminUpdateUser findDataByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return template.findOne(query, AdminUpdateUser.class, "users");
    }

    public long updateUser(UpdateUserDto userDto) throws IllegalAccessException {
        Query query = new Query(Criteria.where("username").is(userDto.getUsername()));
        Update update = new Update();
        Map<String, Object> fields = ObjectFieldsUtils.getFieldsValue(userDto);
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

    public KeyValuePair toggleEnable(String username) {
        Query query = new Query(Criteria.where("_id").is(username));
        DBUser user = template.findOne(query, DBUser.class);
        if (user != null) {
            Update update = new Update();
            update.set("enabled", !user.getEnabled());
            template.updateFirst(query, update, DBUser.class);
            return new KeyValuePair(user.getContactData().get("email"),!user.getEnabled());
        }
        return null;
    }

    public void deleteUser(String username) {
        Query query = new Query(Criteria.where("_id").is(username));
        template.remove(query, "users");
    }

    public TableResult filterUsers(PageRequest pageable, UsersFilterOptions filters, String role, String company) {
        AggregationOperation projectConcatFields = Aggregation.project("firstName", "lastName", "contactData", "address", "company", "role","enabled")
                .andExpression("concat(firstName, ' ', lastName)").as("fullName");
        MatchOperation matchSearchString = Aggregation.match(Criteria.where("fullName").regex(".*" + filters.getFullName() + ".*", "i"));
        MatchOperation filedsMatchOperation = UserRepositoriesUtils.filterUserAggregation(filters, role, company);
        AggregationOperation skip = skip((long) pageable.getPageNumber() * pageable.getPageSize());
        AggregationOperation limit = limit(pageable.getPageSize());
        Aggregation aggregation = newAggregation(
                filedsMatchOperation,
                projectConcatFields,
                matchSearchString,
                Aggregation.facet(Aggregation.count().as("totalCount")).as("countResult")
                        .and(skip, limit).as("paginationResult")
        );
        AggregationResults<TableResult> result = template.aggregate(aggregation,"users", TableResult.class);
        return result.getMappedResults().get(0);
    }

    public List<DBUser> getAll(){
        return template.findAll(DBUser.class,"users");
    }
}
