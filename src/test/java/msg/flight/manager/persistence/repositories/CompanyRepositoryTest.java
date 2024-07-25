package msg.flight.manager.persistence.repositories;

import msg.flight.manager.persistence.models.company.DBCompany;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@DataMongoTest
@ActiveProfiles(profiles = {"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyRepositoryTest {

    CompanyRepository repository = new CompanyRepository();

    @Autowired
    MongoTemplate template;

    @BeforeAll
    public void setUp(){
        this.repository.setTemplate(template);
        template.save(generateCompany("Wizz Air",1),"companies");
        template.save(generateCompany("ITAirways",2),"companies");
        template.save(generateCompany("Tarom",3),"companies");
    }

    @AfterAll
    public void cleanUp(){
        this.template.dropCollection("companies");
    }

    @Test
    @Order(1)
    public void getAll_DBCompanyList_always(){
        List<DBCompany> expected = new ArrayList<>();
        expected.add(generateCompany("Wizz Air",1));
        expected.add(generateCompany("ITAirways",2));
        expected.add(generateCompany("Tarom",3));
        Assertions.assertEquals(expected,this.repository.getAll());
    }

    @Test
    @Order(2)
    public void get_DBCompany_whenItExists(){
        DBCompany expected = generateCompany("Tarom",3);
        Assertions.assertEquals(expected,this.repository.get("Tarom"));
    }

    @Test
    @Order(3)
    public void get_Null_whenItDoesntExist(){
        Assertions.assertNull(this.repository.get("I DONT EXIST"));
    }

    @Test
    @Order(4)
    public void save_DBCompany_whenItDoesntExist(){
        DBCompany expected = generateCompany("Quatar",50);
        Assertions.assertEquals(expected,this.repository.save(expected));
    }

    @Test
    @Order(5)
    public void update_false_whenItDoesntExist(){
        DBCompany updating = generateCompany("WizzAir",10);
        Assertions.assertFalse(this.repository.update("WuzzAir",updating));
    }

    @Test
    @Order(6)
    public void update_true_whenItExists(){
        DBCompany updating = generateCompany("WizzAir",10);
        Assertions.assertTrue(this.repository.update("Wizz Air",updating));
    }

    private DBCompany generateCompany(String name, int fleet){
        return DBCompany.builder()
            .name(name)
            .fleet(fleet).build();
    }
}
