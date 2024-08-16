package msg.flight.manager.services.itineraries;

import msg.flight.manager.persistence.dtos.itinerary.Itinerary;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import msg.flight.manager.persistence.repositories.ItineraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryService {
    @Autowired
    ItineraryRepository repository;

    private Itinerary dbItinerary2Itinerary(DBItinerary itinerary){
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

    private DBItinerary itinerary2DBItinerary(Itinerary itinerary){
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

    public ResponseEntity<?> get(long id){
        DBItinerary dbItinerary = this.repository.get(id);
        if(dbItinerary == null) return new ResponseEntity<String>("Itinerary with given ID not found",HttpStatusCode.valueOf(404));
        return new ResponseEntity<>(this.dbItinerary2Itinerary(dbItinerary),HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> get(){
        List<Itinerary> result = this.repository.get()
                .stream()
                .map(this::dbItinerary2Itinerary)
                .toList();
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> save(){

        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> update(){
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> delete(){
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
}
