package msg.flight.manager.services.planes;

import msg.flight.manager.persistence.dtos.plane.GetPlane;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.dtos.plane.PlaneDataTableView;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaneService {

    private SecurityUserUtil securityUser = new SecurityUserUtil();

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

    public ResponseEntity<?> getCompanyPlanes(){
        SecurityUser loggedUser = securityUser.getLoggedUser();
        String company = loggedUser.getCompany();
        List<DBPlane> raw = this.repository.get();
        List<Plane> result = raw.stream()
                .filter((DBPlane plane) -> plane.getCompany().getName().equals(company))
                .map(this::dbPlane2plane)
                .toList();
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> getFilteredCompanyPlanes(GetPlane request){
        SecurityUser loggedUser = securityUser.getLoggedUser();
        String company = loggedUser.getCompany();
        return new ResponseEntity<>(this.repository.getFilteredByCompanyAlso(PageRequest.of(request.getPage(),request.getSize()),request,company).toPlaneTableResult(PlaneDataTableView.class),HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> get(){
        return new ResponseEntity<>(this.repository.get()
                .stream()
                .map(this::dbPlane2plane)
                .toList(), HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> getFiltered(GetPlane request){
        return new ResponseEntity<>(this.repository.getFiltered(PageRequest.of(request.getPage(),request.getSize()),request).toPlaneTableResult(PlaneDataTableView.class),HttpStatusCode.valueOf(200));
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
