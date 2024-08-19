package msg.flight.manager.services.itineraries;

import msg.flight.manager.persistence.dtos.itinerary.Itinerary;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import msg.flight.manager.persistence.repositories.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryService {
    @Autowired
    ItineraryRepository repository;

    public Itinerary dbItinerary2Itinerary(DBItinerary itinerary){
        return Itinerary
                .builder()
                .ID(itinerary.getId())
                .departure(itinerary.getDep())
                .arrival(itinerary.getArr())
                .departureTime(itinerary.getDepTime())
                .arrivalTime(itinerary.getArrTime())
                .plane(itinerary.getPlane())
                .build();
    }

    public DBItinerary itinerary2DBItinerary(Itinerary itinerary){
        return DBItinerary
                .builder()
                .id(itinerary.getID())
                .dep(itinerary.getDeparture())
                .arr(itinerary.getArrival())
                .depTime(itinerary.getDepartureTime())
                .arrTime(itinerary.getArrivalTime())
                .plane(itinerary.getPlane())
                .build();
    }

    public ResponseEntity<?> get(String id){
        DBItinerary dbItinerary = this.repository.get(id);
        if(dbItinerary == null) return new ResponseEntity<String>("Itinerary with given ID not found",HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(this.dbItinerary2Itinerary(dbItinerary),HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> get(){
        List<Itinerary> result = this.repository.get()
                .stream()
                .map(this::dbItinerary2Itinerary)
                .toList();
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> save(Itinerary itinerary){
        if(repository.get(itinerary.getID()) != null) return new ResponseEntity<>("Itinerary already registered", HttpStatus.NOT_ACCEPTABLE);
        repository.save(itinerary2DBItinerary(itinerary));
        return new ResponseEntity<>("Saved",HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> update(String id, Itinerary itinerary){
        if(this.repository.get(id) == null) return new ResponseEntity<>("Itinerary with given ID doesnt exist",HttpStatus.NOT_FOUND);
        if(!this.repository.delete(id)) return new ResponseEntity<>("Internal error occoured while deleting the itinerary",HttpStatus.INTERNAL_SERVER_ERROR);
        if(this.repository.save(itinerary2DBItinerary(itinerary)) == null) return new ResponseEntity<>("Internal server error occoured while updating the itinerary", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("Itinerary updated",HttpStatus.OK);
    }

    public ResponseEntity<?> delete(String id){
        if(repository.get(id) == null) return new ResponseEntity<>("Itinerary not found", HttpStatus.NOT_FOUND);
        if(repository.delete(id)) return new ResponseEntity<>("Itinerary deleted",HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Internal error occoured while deleting the itinerary",HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
