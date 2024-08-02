package msg.flight.manager.services.planes;

import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaneService {


    @Autowired
    private PlaneRepository repository;

    public DBPlane plane2dbPlane(Plane plane){
        return DBPlane.builder()
                .model(plane.getModel())
                .registrationNumber(plane.getRegistrationNumber())
                .manufacturer(plane.getManufacturer())
                .manufactureYear(plane.getManufactureYear())
                .range(plane.getRange())
                .cruisingSpeed(plane.getCruisingSpeed())
                .wingspan(plane.getWingspan())
                .length(plane.getLength())
                .height(plane.getHeight())
                .company(plane.getCompany())
                .build();
    }

    public Plane dbPlane2plane(DBPlane plane){
        return Plane.builder()
                .model(plane.getModel())
                .registrationNumber(plane.getRegistrationNumber())
                .manufacturer(plane.getManufacturer())
                .manufactureYear(plane.getManufactureYear())
                .range(plane.getRange())
                .cruisingSpeed(plane.getCruisingSpeed())
                .wingspan(plane.getWingspan())
                .length(plane.getLength())
                .height(plane.getHeight())
                .company(plane.getCompany())
                .build();
    }

    public ResponseEntity<?> get(){
        return new ResponseEntity<>(this.repository.get()
                .stream()
                .map(this::dbPlane2plane)
                .toList(), HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> get(String registrationNumber){
        DBPlane result = this.repository.get(registrationNumber);
        if(result != null) {
            return new ResponseEntity<>(dbPlane2plane(result), HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>("Unable to find plane with " + registrationNumber +" registration number",HttpStatusCode.valueOf(404));
    }

    public ResponseEntity<?> save(Plane plane){
        if(this.repository.save(plane2dbPlane(plane))) return new ResponseEntity<>("Plane saved", HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Plane with the same registration number found",HttpStatusCode.valueOf(403));
    }

    public ResponseEntity<?> update(String registrationNumber, Plane plane){
        this.repository.update(registrationNumber, plane2dbPlane(plane));
        return new ResponseEntity<>("Plane updated", HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> delete(String registrationNumber){
        if(this.repository.delete(registrationNumber)) return new ResponseEntity<>("Plane deleted", HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Unable to delete plane",HttpStatusCode.valueOf(404));
    }

}
