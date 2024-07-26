package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.airport.Airport;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.repositories.AirportRepository;
import msg.flight.manager.services.airports.AirportService;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AirportServiceTest {
    @InjectMocks
    AirportService service;

    @Mock
    AirportRepository repository;

    @BeforeAll
    public void setUp(){
        this.service = new AirportService();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void airport2DBAirport_DBAirport_always(){
        DBAirport expected = generateDBAirport("A","A","A","A",new HashMap<>());
        Airport converting = generateAirport("A","A","A","A",new HashMap<>());
        Assertions.assertEquals(expected,this.service.airport2DBAirport(converting));
    }

    @Test
    public void dbAirport2Airport_Airport_always(){
        DBAirport converting = generateDBAirport("A","A","A","A",new HashMap<>());
        Airport expected = generateAirport("A","A","A","A",new HashMap<>());
        Assertions.assertEquals(expected,this.service.dbAirport2Airport(converting));
    }

    @Test
    public void getAirports_AirportList_always(){
        List<DBAirport> retVal = new ArrayList<>();
        retVal.add(generateDBAirport("A","A","A","A",new HashMap<>()));
        retVal.add(generateDBAirport("B","B","B","B",new HashMap<>()));
        retVal.add(generateDBAirport("C","C","C","C",new HashMap<>()));
        List<Airport> expected = retVal.stream().map(service::dbAirport2Airport).toList();
        Mockito.when(this.repository.get()).thenReturn(retVal);
        ResponseEntity<?> response = this.service.getAirports();
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void getAirport_Airport_whenItExists(){
        DBAirport mock = generateDBAirport("A","A","A","A",new HashMap<>());
        Airport expected = generateAirport("A","A","A","A",new HashMap<>());
        Mockito.when(this.repository.get("A")).thenReturn(mock);
        ResponseEntity<?> response = this.service.getAirport("A");
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void getAirport_Airport_whenItDoesntExist(){
        String expected = "Airport not found";
        Mockito.when(this.repository.get("A")).thenReturn(null);
        ResponseEntity<?> response = this.service.getAirport("A");
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(404),response.getStatusCode());
    }

    @Test
    public void saveAirport_String_whenItSaves(){
        Airport saving = generateAirport("A","A","A","A",new HashMap<>());
        DBAirport mock = generateDBAirport("A","A","A","A",new HashMap<>());
        String expected = "Airport saved!";
        Mockito.when(this.repository.save(mock)).thenReturn(true);
        ResponseEntity<?> response = this.service.saveAirport(saving);
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void saveAirport_String_whenItFails(){
        Airport saving = generateAirport("A","A","A","A",new HashMap<>());
        DBAirport mock = generateDBAirport("A","A","A","A",new HashMap<>());
        String expected = "Unable to save airport!";
        Mockito.when(this.repository.save(mock)).thenReturn(false);
        ResponseEntity<?> response = this.service.saveAirport(saving);
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(400),response.getStatusCode());
    }

    @Test
    public void deleteAirport_String_whenItDeletes(){
        String expected = "Airport deleted!";
        Mockito.when(this.repository.delete("A")).thenReturn(true);
        ResponseEntity<?> response = this.service.deleteAirport("A");
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void deleteAirport_String_whenItFails(){
        String expected = "Unable to delete airport!";
        Mockito.when(this.repository.delete("A")).thenReturn(false);
        ResponseEntity<?> response = this.service.deleteAirport("A");
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(400),response.getStatusCode());
    }

    @Test
    public void updateAirport_String_whenItUpdates(){
        Airport updating = generateAirport("A","A","A","A",new HashMap<>());
        DBAirport mock = generateDBAirport("A","A","A","A",new HashMap<>());
        String expected = "Airport updated!";
        Mockito.when(this.repository.update("A",mock)).thenReturn(true);
        ResponseEntity<?> response = this.service.updateAirport("A",updating);
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void updateAirport_String_whenItFails(){
        Airport updating = generateAirport("A","A","A","A",new HashMap<>());
        DBAirport mock = generateDBAirport("A","A","A","A",new HashMap<>());
        String expected = "Unable to update the airport!";
        Mockito.when(this.repository.update("A",mock)).thenReturn(false);
        ResponseEntity<?> response = this.service.updateAirport("A",updating);
        Assertions.assertEquals(expected,response.getBody());
        Assertions.assertEquals(HttpStatusCode.valueOf(400),response.getStatusCode());
    }

    private Airport generateAirport(String icao, String iata, String name, String location, Map<String,String> contactData){
        return Airport
                .builder()
                .icao(icao)
                .iata(iata)
                .name(name)
                .location(location)
                .contactData(contactData)
                .build();
    }
    private DBAirport generateDBAirport(String icao,String iata,String name, String location, Map<String,String> contactData){
        return DBAirport
                .builder()
                .icao(icao)
                .iata(iata)
                .name(name)
                .location(location)
                .contactData(contactData)
                .build();
    }

}
