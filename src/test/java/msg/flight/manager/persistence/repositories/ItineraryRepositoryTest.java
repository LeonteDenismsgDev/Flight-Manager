package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItineraryRepositoryTest {

    private final ItineraryRepository repository = new ItineraryRepository();

    @Autowired
    private MongoTemplate template;

    @BeforeAll
    public void setUp(){
        this.repository.setTemplate(template);
        this.template.insert(this.generate(1,"A","B",0,1));
        this.template.insert(this.generate(2,"C","D",2,3));
        this.template.insert(this.generate(3,"E","F",4,5));
        this.template.insert(this.generate(4,"G","H",6,7));
    }

    @AfterAll
    public void cleanUp(){
        this.template.dropCollection("itineraries");
    }

    @Test
    @Order(1)
    public void get_correctItinerary_whenItineraryIsPresent(){
        DBItinerary expected = this.generate(2,"C","D",2,3);
        Assertions.assertEquals(expected,this.repository.get(2));
    }

    @Test
    @Order(2)
    public void get_null_whenItineraryIsNotPresent(){
        Assertions.assertNull(this.repository.get(999));
    }

    @Test
    @Order(3)
    public void get_allItineraries_always(){
        List<DBItinerary> expected = new ArrayList<>();
        expected.add(this.generate(1,"A","B",0,1));
        expected.add(this.generate(2,"C","D",2,3));
        expected.add(this.generate(3,"E","F",4,5));
        expected.add(this.generate(4,"G","H",6,7));
        Assertions.assertEquals(expected,this.repository.get());
    }

    @Test
    @Order(4)
    public void save_successfull_ifIDIsNotPresent(){
        DBItinerary expected= generate(5,"I","J",8,9);
        Assertions.assertEquals(expected,this.repository.save(expected));
    }

    @Test
    @Order(5)
    public void save_failure_ifIDIsPresent(){
        DBItinerary itinerary = generate(3,"I","J",8,9);
        Assertions.assertThrows(DuplicateKeyException.class,()->this.repository.save(itinerary));
    }

    @Test
    @Order(6)
    public void delete_successfull_whenIDIsPresent(){
        Assertions.assertTrue(this.repository.delete(5));
        Assertions.assertNull(this.repository.get(5));
    }

    @Test
    @Order(6)
    public void delete_failure_whenIDIsNotPresent(){
        Assertions.assertFalse(this.repository.delete(999));
    }

    @Test
    @Order(7)
    public void update_success_whenIDIsPresent(){
        DBItinerary expected = generate(4,"Y","Z",6,7);
        Assertions.assertTrue(this.repository.update(4,expected));
        Assertions.assertEquals(expected,this.repository.get(4));
    }

    @Test
    @Order(8)
    public void update_failure_whenIDIsNotPresent(){
        DBItinerary expected = generate(4,"Y","Z",6,7);
        DBItinerary updated = generate(4,"AA","BB",6,7);
        Assertions.assertFalse(this.repository.update(999,updated));
        Assertions.assertEquals(expected,this.repository.get(4));
    }

    @Test
    @Order(9)
    public void update_failure_whenIDIsPresentButUpdatedItineraryIDExists(){
        DBItinerary expected = generate(4,"Y","Z",6,7);
        DBItinerary updating = generate(3,"AA","BB",6,7);
        Assertions.assertThrows(DuplicateKeyException.class,()->this.repository.update(4,updating));
        Assertions.assertEquals(expected,this.repository.get(4));
    }

    private DBItinerary generate(long id,String dep, String arr, long depTime, long arrTime){
        return DBItinerary
                .builder()
                .id(id)
                .dep(dep)
                .arr(arr)
                .depTime(new Date(depTime))
                .arrTime(new Date(arrTime))
                .plane(new Plane())
                .build();
    }
}
