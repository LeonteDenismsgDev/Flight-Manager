package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.airport.DBAirport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AirportRepositoryTest {
    AirportRepository repository = new AirportRepository();

    @Autowired
    MongoTemplate template;

    @BeforeAll
    public void setUp(){
        this.repository.setTemplate(template);
        this.template.save(generateAirport("LIMC","MXP","Milan Malpensa Airport","Milan",new HashMap<>()));
        this.template.save(generateAirport("LIPZ","VCE","Venice Marco Polo Airport","Venice",new HashMap<>()));
        this.template.save(generateAirport("LIRN","NAP","Naples International Airport","Naples",new HashMap<>()));
        this.template.save(generateAirport("LIMJ","GOA","Genoa Cristoforo Colombo Airport","Genoa",new HashMap<>()));
        this.template.save(generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>()));
    }

    @AfterAll
    public void cleanUp(){
        this.template.dropCollection("airports");
    }

    @Test
    @Order(1)
    public void get_ListOfDBAirport_always(){
        List<DBAirport> expected = new ArrayList<>();
        expected.add(generateAirport("LIMC","MXP","Milan Malpensa Airport","Milan",new HashMap<>()));
        expected.add(generateAirport("LIPZ","VCE","Venice Marco Polo Airport","Venice",new HashMap<>()));
        expected.add(generateAirport("LIRN","NAP","Naples International Airport","Naples",new HashMap<>()));
        expected.add(generateAirport("LIMJ","GOA","Genoa Cristoforo Colombo Airport","Genoa",new HashMap<>()));
        expected.add(generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>()));
        Assertions.assertEquals(expected,this.repository.get());
    }

    @Test
    @Order(2)
    public void get_DBAirport_whenItExists(){
        DBAirport expected = generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>());
        Assertions.assertEquals(expected,this.repository.get("LIRF"));
    }

    @Test
    @Order(3)
    public void get_null_whenItDoesntExist(){
        Assertions.assertNull(this.repository.get("I DONT EXIST"));
    }

    @Test
    @Order(4)
    public void save_true_whenItDoesntExist(){
        DBAirport airport = generateAirport("LIRA","CIA","Ciampino–G. B. Pastine International Airport","Rome",new HashMap<>());
        Assertions.assertTrue(this.repository.save(airport));
        Assertions.assertEquals(airport,this.repository.get("LIRA"));
    }

    @Test
    @Order(5)
    public void save_false_whenItExists(){
        DBAirport airport = generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>());
        Assertions.assertFalse(this.repository.save(airport));
    }

    @Test
    @Order(6)
    public void delete_true_whenItExists(){
        Assertions.assertTrue(this.repository.delete("LIRA"));
        Assertions.assertNull(this.repository.get("LIRA"));
    }

    @Test
    @Order(7)
    public void delete_false_whenItDoesntExist(){
        Assertions.assertFalse(this.repository.delete("LIRA"));
    }

    @Test
    @Order(8)
    public void update_true_whenItExists(){
        DBAirport airport = generateAirport("LIRF","FCO","Leonardo da Vinci Fiumicino Airport","Rome",new HashMap<>());
        Assertions.assertTrue(this.repository.update("LIRF",airport));
        Assertions.assertEquals(airport,this.repository.get("LIRF"));
    }

    @Test
    @Order(9)
    public void update_false_whenItDoesntExist(){
        DBAirport airport = generateAirport("LIRF","FCO","Leonardo da Vinci–Fiumicino Airport","Rome",new HashMap<>());
        Assertions.assertFalse(this.repository.update("I DONT EXIST",airport));
    }

    private DBAirport generateAirport(String icao,String iata,String name, String location, Map<String,String> contactData){
        return DBAirport
                .builder()
                .icao(icao)
                .iata(iata)
                .airportName(name)
                .location(location)
                .contactData(contactData)
                .build();
    }
}
