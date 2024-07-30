package msg.flight.manager.services.airports;

import msg.flight.manager.persistence.dtos.airport.Airport;
import msg.flight.manager.persistence.dtos.airport.AirportDataTableView;
import msg.flight.manager.persistence.dtos.airport.AirportTableResult;
import msg.flight.manager.persistence.dtos.airport.GetAirport;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.repositories.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AirportService {
    @Autowired
    private AirportRepository repository;

    public DBAirport airport2DBAirport(Airport airport){
        return DBAirport.builder()
                .icao(airport.getIcao())
                .iata(airport.getIata())
                .airportName(airport.getAirportName())
                .location(airport.getLocation())
                .contactData(airport.getContactData())
                .build();
    }

    public Airport dbAirport2Airport(DBAirport airport){
        return Airport.builder()
                .icao(airport.getIcao())
                .iata(airport.getIata())
                .airportName(airport.getAirportName())
                .location(airport.getLocation())
                .contactData(airport.getContactData())
                .build();
    }

    public ResponseEntity<?> prepare(){
        this.repository.prepare();
        return new ResponseEntity<>("Prepared", HttpStatus.OK);
    }

    public ResponseEntity<?> getAirports(){
        return new ResponseEntity<>(this.repository.get().stream().map(this::dbAirport2Airport).toList(), HttpStatusCode.valueOf(200));
    }

    public AirportTableResult getAirportsFiltered(GetAirport request){
        return this.repository.getFilteredList(PageRequest.of(request.getPage(), request.getSize()),request).toAirportTableResult(AirportDataTableView.class);
    }

    public ResponseEntity<?> getAirport(String icao){
        DBAirport result = repository.get(icao);
        if(result == null) return new ResponseEntity<>("Airport not found",HttpStatusCode.valueOf(404));
        return new ResponseEntity<>(this.dbAirport2Airport(result),HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> saveAirport(Airport airport){
        if(repository.save(airport2DBAirport(airport)))return new ResponseEntity<>("Airport saved!", HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Unable to save airport!",HttpStatusCode.valueOf(400));
    }

    public ResponseEntity<?> deleteAirport(String icao){

        if(repository.delete(icao))return new ResponseEntity<>("Airport deleted!", HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Unable to delete airport!",HttpStatusCode.valueOf(400));
    }

    public ResponseEntity<?> updateAirport(String icao, Airport airport){
        if(repository.update(icao,airport2DBAirport(airport)))return new ResponseEntity<>("Airport updated!", HttpStatusCode.valueOf(200));
        return new ResponseEntity<>("Unable to update the airport!",HttpStatusCode.valueOf(400));
    }
}
