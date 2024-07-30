package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import msg.flight.manager.services.planes.PlaneService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class PlaneServiceTest {
    @Mock
    private PlaneRepository repository;

    @InjectMocks
    private PlaneService service;

    @Before
    public void setUp(){
        service = new PlaneService();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void plane2dbPlane_convertsSuccessfully_whenPlaneGiven(){
        Plane plane = generatePlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        DBPlane expected = generateDBPlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        Assert.assertEquals(expected,this.service.plane2dbPlane(plane));
    }

    @Test
    public void dbPlane2plane_convertsSuccessfully_whenPlaneGiven(){
        DBPlane plane = generateDBPlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        Plane expected = generatePlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        Assert.assertEquals(expected,this.service.dbPlane2plane(plane));
    }

    @Test
    public void get_Plane_whenPlaneExists(){
        DBPlane plane = generateDBPlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        Mockito.when(this.repository.get("A")).thenReturn(plane);
        ResponseEntity<?> response = this.service.get("A");
        Assert.assertEquals(this.service.dbPlane2plane(plane),response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void get_Plane_whenPlaneIsNull(){
        Mockito.when(this.repository.get("A")).thenReturn(null);
        ResponseEntity<?> response = this.service.get("A");
        Assert.assertEquals("Unable to find plane with A registration number",response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(404),response.getStatusCode());
    }

    @Test
    public void get_PlaneList_whenPlaneExists(){
        DBPlane plane = generateDBPlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        List<DBPlane> planeList = new ArrayList<>();
        planeList.add(plane);
        planeList.add(plane);
        Mockito.when(this.repository.get()).thenReturn(planeList);
        List<Plane> expectedList = planeList.stream().map(this.service::dbPlane2plane).toList();
        ResponseEntity<?> response = this.service.get();
        Assert.assertEquals(expectedList,response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void get_PlaneList_whenRepositoryEmpty(){

        Mockito.when(this.repository.get()).thenReturn(new ArrayList<>());
        List<Plane> expectedList = new ArrayList<>();
        ResponseEntity<?> response = this.service.get();
        Assert.assertEquals(expectedList,response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void save_true_whenPlaneDoesntExist(){
        Plane plane = generatePlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        DBPlane dbPlane = generateDBPlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        Mockito.when(this.repository.save(dbPlane)).thenReturn(true);
        ResponseEntity<?> response = this.service.save(plane);
        Assert.assertEquals("Plane saved",response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void save_false_whenPlaneExists(){
        Plane plane = generatePlane("A","A","A",1,1,new Company("",0,new HashMap<>(),0),1,1,1,1);
        ResponseEntity<?> response = this.service.save(plane);
        Assert.assertEquals("Plane with the same registration number found",response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(403),response.getStatusCode());
    }

    @Test
    public void delete_true_whenPlaneExists(){
        Mockito.when(this.repository.delete("A")).thenReturn(true);
        ResponseEntity<?> response = this.service.delete("A");
        Assert.assertEquals("Plane deleted",response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void delete_false_whenPlaneDoesntExists(){
        Mockito.when(this.repository.delete("A")).thenReturn(false);
        ResponseEntity<?> response = this.service.delete("A");
        Assert.assertEquals("Unable to delete plane",response.getBody());
        Assert.assertEquals(HttpStatusCode.valueOf(404),response.getStatusCode());
    }


    private Plane generatePlane(String model, String manufacturer ,String registrationNumber, int manufactureYear, int range,Company company, int cruisingspeed, float height,float length,float wingspan){
        return Plane.builder()
                .model(model)
                .range(range)
                .company(company)
                .cruisingSpeed(cruisingspeed)
                .height(height)
                .length(length)
                .manufacturer(manufacturer)
                .registrationNumber(registrationNumber)
                .manufactureYear(manufactureYear)
                .wingspan(wingspan)
                .build();
    }

    private DBPlane generateDBPlane(String model, String manufacturer ,String registrationNumber, int manufactureYear, int range,Company company, int cruisingspeed, float height,float length,float wingspan){
        return DBPlane.builder()
                .model(model)
                .range(range)
                .company(company)
                .cruisingSpeed(cruisingspeed)
                .height(height)
                .length(length)
                .manufacturer(manufacturer)
                .registrationNumber(registrationNumber)
                .manufactureYear(manufactureYear)
                .wingspan(wingspan)
                .build();
    }
}
