package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.security.SecurityUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<DBUser, String> {
    @Query(value="{ 'username': ?0 }", fields="{ 'username' : 1, 'password' : 1, 'enabled' : 1, 'role' : 1}")
    Optional<SecurityUser> findByUsername(String username);
}
