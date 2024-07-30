package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.models.plane.DBPlane;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlaneRepositoryTest {

    private PlaneRepository repository = new PlaneRepository();

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeAll
    public void setup(){
        this.repository.setTemplate(mongoTemplate);
        this.mongoTemplate.save(generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())),"planes");
        this.mongoTemplate.save(generateDBPlane("A320","HA5465",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())),"planes");
        this.mongoTemplate.save(generateDBPlane("A320","HA8964",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())),"planes");

    }

    @AfterAll
    public void cleanUp(){
        this.mongoTemplate.dropCollection("planes");
    }


    //this must run first
    @Test
    @Order(1)
    public void get_listOfDBPlanes_always(){
        List<DBPlane> expected = new ArrayList<>();
        expected.add(generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())));
        expected.add(generateDBPlane("A320","HA5465",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())));
        expected.add(generateDBPlane("A320","HA8964",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>())));
        Assertions.assertEquals(expected, this.repository.get());
    }

    @Test
    @Order(3)
    public void get_DBPlane_whenRegistrationNumberExists(){
        DBPlane expected = generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>()));
        Assertions.assertEquals(expected,this.repository.get("HA3042"));
    }

    @Test
    public void get_null_whenRegistrationNumberDoesntExist(){
        Assertions.assertNull(this.repository.get("I DO NOT EXIST"));
    }

    @Test
    @Order(2)
    public void save_true_whenDBPlaneDoesntExist(){
        DBPlane expected = generateDBPlane("A320","IT5932",2020,2000,1000,30.5f,40,13,new Company("ALITALIA",0,new HashMap<>()));
        Assertions.assertTrue(this.repository.save(expected));
        Assertions.assertEquals(expected,this.repository.get("IT5932"));
    }

    @Test
    @Order(4)
    public void save_false_whenDBPlaneExists(){
        DBPlane existing = generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("ALITALIA",0,new HashMap<>()));
        DBPlane previous = generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("Wizz Air",0,new HashMap<>()));
        Assertions.assertFalse(this.repository.save(existing));
        Assertions.assertEquals(previous,this.repository.get("HA3042"));
    }

    @Test
    public void delete_true_whenDBPlaneExists(){
        Assertions.assertTrue(this.repository.delete("HA3042"));
        Assertions.assertNull(this.repository.get("HA3042"));
    }

    @Test
    public void delete_false_whenDBPlaneDoesntExist(){
        Assertions.assertFalse(this.repository.delete("I DONT EXIST"));
    }

    @Test
    @Order(5)
    public void update_RuntimeException_whenDBPlaneDoesntExist(){
        Exception thrown = Assertions.assertThrows(RuntimeException.class,()->{
            this.repository.update("I DONT EXIST",generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("ALITALIA",0,new HashMap<>())));
        });
        Assertions.assertEquals("Unable to update the plane",thrown.getMessage());
    }

    @Test
    @Order(6)
    public void update_success_whenDBPlaneExists(){
        DBPlane expected = generateDBPlane("A320","HA3042",2020,2000,1000,30.5f,40,13,new Company("ALITALIA",0,new HashMap<>()));
        this.repository.update("HA3042",expected);
        Assertions.assertEquals(expected,this.repository.get("HA3042"));
    }

    private DBPlane generateDBPlane(String model,
                                    String registrationNumber,
                                    int manufactureYear,
                                    int range,
                                    int cruisingSpeed,
                                    float wingspan,
                                    float length,
                                    float height,
                                    Company company){
        return DBPlane.builder()
                .model(model)
                .registrationNumber(registrationNumber)
                .manufactureYear(manufactureYear)
                .range(range)
                .cruisingSpeed(cruisingSpeed)
                .wingspan(wingspan)
                .length(length)
                .height(height)
                .company(company)
                .build();
    }
}
