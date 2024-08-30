package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.itinerary.Itinerary;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import msg.flight.manager.persistence.repositories.ItineraryRepository;
import msg.flight.manager.persistence.utils.TimeHelper;
import msg.flight.manager.services.itineraries.ItineraryService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class ItineraryServiceTest {
    @Mock
    ItineraryRepository repository;

    @InjectMocks
    ItineraryService service;

    @BeforeAll
    public void setUp(){
        this.service = new ItineraryService();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void dbItinerary2Itinerary_Itinerary_always(){
        DBItinerary itinerary = generateDB("1","a","b");
        Itinerary expected = generate("1","a","b");
        Assertions.assertEquals(expected, ItineraryService.dbItinerary2Itinerary(itinerary));
    }


    @Test
    public void dbItinerary2Itinerary_Itinerary_whenIDIsNull(){
        DBItinerary itinerary = generateDB(null,"a","b");
        Itinerary expected = generate(null,"a","b");
        Assertions.assertEquals(expected, ItineraryService.dbItinerary2Itinerary(itinerary));
    }

    @Test
    public void itinerary2DBItinerary_DBItinerary_always(){
        Itinerary itinerary = generate("1","a","b");
        DBItinerary expected = generateDB("1","a","b");
        Assertions.assertEquals(expected,this.service.itinerary2DBItinerary(itinerary));
    }


    @Test
    public void itinerary2DBItinerary_DBItinerary_whenIDIsNull(){
        Itinerary itinerary = generate(null,"a","b");
        DBItinerary expected = generateDB(null,"a","b");
        Assertions.assertEquals(expected,this.service.itinerary2DBItinerary(itinerary));
    }

    @Test
    public void get_Itinerary_whenIDExists(){
        when(this.repository.get("1")).thenReturn(generateDB("1","a","b"));
        ResponseEntity<?> response = this.service.get("1");
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assertions.assertEquals(generate("1","a","b"),response.getBody());
    }

    @Test
    public void get_Itinerary_whenIDDoesntExists(){
        when(this.repository.get("1")).thenReturn(null);
        ResponseEntity<?> response = this.service.get("1");
        Assertions.assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
        Assertions.assertEquals("Itinerary with given ID not found",response.getBody());
    }

    @Test
    public void getAll_ItineraryList_always(){
        List<DBItinerary> results = new ArrayList<>();
        results.add(generateDB("1","a","b"));
        results.add(generateDB("2","a","b"));
        List<Itinerary> expected = new ArrayList<>();
        expected.add(generate("1","a","b"));
        expected.add(generate("2","a","b"));
        when(this.repository.get()).thenReturn(results);
        ResponseEntity<?> response = this.service.get();
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assertions.assertEquals(expected,response.getBody());
    }

    @Test
    public void save_failMessage_whenItineraryNull(){
        ResponseEntity<?> response = this.service.save(null);
        Assertions.assertEquals("Null itinerary not permitted",response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void save_failMessage_whenItineraryAlreadyThere(){
        DBItinerary dbitinerary = generateDB("1","a","b");
        Itinerary itinerary = generate("1","a","b");
        when(this.repository.get("1")).thenReturn(dbitinerary);
        ResponseEntity<?> response = this.service.save(itinerary);
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE,response.getStatusCode());
        Assertions.assertEquals("Itinerary already registered",response.getBody());
    }

    @Test
    public void save_successMessage_whenItineraryNotThere(){
        Itinerary itinerary = generate("1","a","b");
        when(this.repository.get("1")).thenReturn(null);
        ResponseEntity<?> response = this.service.save(itinerary);
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assertions.assertEquals("Saved",response.getBody());
    }

    @Test
    public void update_failMessage_whenItineraryNotExistent(){
        when(this.repository.get("1")).thenReturn(null);
        Itinerary itinerary = generate("1","a","b");
        ResponseEntity<?> response = this.service.update("1",itinerary);
        Assertions.assertEquals("Itinerary with given ID doesnt exist",response.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void update_failMessage_whenItineraryNull(){
        ResponseEntity<?> response = this.service.update("1",null);
        Assertions.assertEquals("Null itinerary not permitted",response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void update_failMessage_whenDeletionFails(){
        DBItinerary dbItinerary = generateDB("1","a","b");
        Itinerary itinerary = generate("1","a","b");
        when(this.repository.get("1")).thenReturn(dbItinerary);
        when(this.repository.delete("1")).thenReturn(false);
        ResponseEntity<?> response = this.service.update("1",itinerary);
        Assertions.assertEquals("Internal error occoured while deleting the itinerary",response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void update_failMessage_whenSaveFails(){
        DBItinerary dbItinerary = generateDB("1","a","b");
        Itinerary itinerary = generate("1","a","b");
        when(this.repository.get("1")).thenReturn(dbItinerary);
        when(this.repository.delete("1")).thenReturn(true);
        when(this.repository.save(dbItinerary)).thenReturn(null);
        ResponseEntity<?> response = this.service.update("1",itinerary);
        Assertions.assertEquals("Internal server error occoured while updating the itinerary",response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }

    @Test
    public void update_successMessage_whenUpdateSucceeds(){
        DBItinerary dbItinerary = generateDB("1","a","b");
        Itinerary itinerary = generate("1","a","b");
        when(this.repository.get("1")).thenReturn(dbItinerary);
        when(this.repository.delete("1")).thenReturn(true);
        when(this.repository.save(dbItinerary)).thenReturn(dbItinerary);
        ResponseEntity<?> response = this.service.update("1",itinerary);
        Assertions.assertEquals("Itinerary updated",response.getBody());
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    private DBItinerary generateDB(String id, String dep, String arr){
        return DBItinerary
                .builder()
                .id(id)
                .dep(dep)
                .arr(arr)
                .depTime(TimeHelper.builder().build())
                .arrTime(TimeHelper.builder().build())
                .build();
    }

    private Itinerary generate(String id,String dep, String arr){
        return Itinerary
                .builder()
                .ID(id)
                .departure(dep)
                .arrival(arr)
                .departureTime(TimeHelper.builder().build())
                .arrivalTime(TimeHelper.builder().build())
                .build();
    }
}
