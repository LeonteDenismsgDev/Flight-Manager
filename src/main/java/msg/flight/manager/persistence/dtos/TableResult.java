package msg.flight.manager.persistence.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.airport.Airport;
import msg.flight.manager.persistence.dtos.airport.AirportDataTableView;
import msg.flight.manager.persistence.dtos.airport.AirportTableResult;
import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.dtos.flights.TemplateTableResult;
import msg.flight.manager.persistence.dtos.flights.attributes.TemplateAttribute;
import msg.flight.manager.persistence.dtos.flights.templates.RegisterTemplate;
import msg.flight.manager.persistence.dtos.itinerary.Itinerary;
import msg.flight.manager.persistence.dtos.itinerary.ItineraryDataView;
import msg.flight.manager.persistence.dtos.itinerary.ItineraryTableResult;
import msg.flight.manager.persistence.dtos.plane.Plane;
import msg.flight.manager.persistence.dtos.plane.PlaneDataTableView;
import msg.flight.manager.persistence.dtos.plane.PlaneTableResult;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.dtos.user.update.UserTableResult;
import msg.flight.manager.persistence.models.itinerary.DBItinerary;
import msg.flight.manager.services.itineraries.ItineraryService;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.boot.json.GsonJsonParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TableResult {
    private List<Document> countResult;
    private List<Document> paginationResult;

    public UserTableResult toUserTableResult(Class<? extends UpdateUserDto> listClass) {
        return UserTableResult.builder()
                .usersCount(countResult.get(0).get("totalCount", Integer.class))
                .page(paginationResult.stream().map(doc -> {
                    try {
                        return convertDocumentToDto(doc, listClass);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()))
                .build();
    }

    public TemplateTableResult toTemplateTableResult(Class<? extends RegisterTemplate> listClass) {
        return TemplateTableResult.builder()
                .templatesCount(countResult.get(0).get("totalCount", Integer.class))
                .page(paginationResult.stream().map(doc -> {
                    try {
                        return convertDocumentToDto(doc, listClass);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()))
                .build();
    }

    public AirportTableResult toAirportTableResult(Class <AirportDataTableView> listClass) {
        try {
            AirportTableResult result = AirportTableResult.builder()
                    .max_airports(countResult.get(0).get("totalCount", Integer.class))
                    .page(paginationResult.stream().map(doc -> {
                        try {
                            return convertDocumentToDto(doc, listClass);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).toList())
                    .build();
            return result;
        }catch (IndexOutOfBoundsException ex){
            return new AirportTableResult(0,new ArrayList<>());
        }
    }

    public ItineraryTableResult toItineraryTableResult(Class <Itinerary> listClass){
        try{
            ItineraryTableResult result = ItineraryTableResult.builder()
                    .max_itineraries(countResult
                            .get(0).get("totalCount",Integer.class))
                    .page(new ArrayList<Itinerary>( paginationResult.stream().map(doc ->{
                        try{
                            return convertDocumentToDto(doc, DBItinerary.class);
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    })
                    .map(ItineraryService::dbItinerary2Itinerary).toList()))
                    .build();
            return result;
        }catch (IndexOutOfBoundsException ex){
            return new ItineraryTableResult(0,new ArrayList<>());
        }
    }

    public PlaneTableResult toPlaneTableResult(Class <PlaneDataTableView> listClass){
        try{
            PlaneTableResult result = PlaneTableResult.builder()
                    .max_planes(countResult.get(0).get("totalCount",Integer.class))
                    .page(paginationResult.stream().map(doc ->{
                        try{
                            return convertDocumentToDto(doc,listClass);
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }).toList())
                    .build();
            return result;
        }catch (IndexOutOfBoundsException ex){
            return new PlaneTableResult(0,new ArrayList<>());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertDocumentToDto(Document document, Class<T> clazz) throws Exception {
        T dto = clazz.getDeclaredConstructor().newInstance();
        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            if (field.getName().equals("attributes")) {
                List<Document> bsonList = (List<Document>) document.get(field.getName(), ArrayList.class);
                Set<TemplateAttribute> attributes = bsonList.stream()
                        .map(doc -> new TemplateAttribute(BsonDocument.parse(doc.toJson())))
                        .collect(Collectors.toSet());
                field.set(dto, attributes);
            } else if (field.getName().equals("validations")) {
                List<Document> bsonList = (List<Document>) document.get(field.getName(), ArrayList.class);
                List<JsonObject> validations = bsonList.stream()
                        .map(doc -> new JsonObject(doc.toJson()))
                        .collect(Collectors.toList());
                field.set(dto, validations);
            } else if(field.getType() == Company.class){
                Document bsonDocument = (Document) document.get(field.getName(),Document.class);
                ObjectMapper mapper = new ObjectMapper();
                Company company = mapper.readValue(bsonDocument.toJson(),Company.class);
                field.set(dto,company);
            }
            else {
                Object object = document.get(field.getName(), field.getType());
                if (field.getName().equals("username") || field.getName().equals("name") || field.getName().equals("icao") || field.getName().equals("registrationNumber")) {
                    object = document.get("_id", field.getType());
                }
                if(field.getType() == Double.class){
                    Double _d = (Double) object;
                    _d = (double) Math.round(_d * 100)/100;
                    field.set(dto,_d);
                }
                else if(field.getType() == Float.class){
                    Float _f = (Float) object;
                    _f = (float) Math.round(_f*100)/100;
                    field.set(dto,_f);
                }
                else {
                    field.set(dto, object);
                }
            }

        }
        return dto;
    }

    public List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
