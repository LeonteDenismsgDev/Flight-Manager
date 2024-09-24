package msg.flight.manager.services.flights.validation.predefinedTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import msg.flight.manager.persistence.repositories.ValidationRepository;
import msg.flight.manager.persistence.repositories.WorkHoursRepository;
import msg.flight.manager.services.flights.validation.template.predefinedTypes.PredefinedTypeValidator;
import org.bson.Document;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PredefinedValidatorTest {
    @Mock
    private ValidationRepository validationRepository;
    @Mock
    private WorkHoursRepository workHoursRepository;
    @InjectMocks
    private PredefinedTypeValidator predefinedValidator;

    @Test
    public void checkAirport_returnsInvalidAirportResponse_whenFlightNodeIsNotObject() {
        String expected = "invalidAirportType should be a valid airport\n";
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "invalidAirportType"));
    }

    @Test
    public void checkAirport_returnsInvalidAirportResponse_whenInvalidFlightStructureAndLocation() {
        String expected = "Invalid invalidAirportStructure airport\n";
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "invalidAirportStructure"));
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(true,false), "invalidAirportStructure"));
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,true), "invalidAirportStructure"));
    }

    @Test
    public void checkAirport_returnsInvalidKeyResponse_whenFlightObjectLackAttributeKey() {
        String expected = "The flight doesn't contain any filed named invalidKey\n";
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "invalidKey"));
    }

    @Test
    public void checkAirport_returnsInvalidAirportResponse_whenInvalidFlightId() {
        String expected = "Invalid invalidAirportId airport\n";
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "invalidAirportId"));
    }

    @Test
    public void checkAirport_returnNonExistentAirport_whenAirportNonFound() {
        String expected = "There is no similar airport registered\n";
      //  Mockito.when(validationRepository.getObject("airports", "airportId")).thenReturn(null);
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "nonExistentAirport"));
    }

    @Test
    public void checkAirport_returnsEmptyString_whenValidFight() {
        String expected = "";
       // Mockito.when(validationRepository.getObject("airports", "airportId")).thenReturn(new Document());
        //Assertions.assertEquals(expected, predefinedValidator.checkAirport(createJsonNodeFlight(false,false), "nonExistentAirport"));
    }

    @Test
    public void checkForTime_returnsMissingFiledResponse_nonExistentFlight(){
        String expected =  "Flight doesn't have any filed with invalidKey name\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkForTime(createJsonNodeFlight(false,false),"invalidKey"));
    }

    @Test
    public void checkForTime_returnsInvalidTypeResponse_whenWrongDateTimeType(){
        String expected = "Invalid type for dateTimeType\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkForTime(createJsonNodeFlight(false,false),"dateTimeType"));
    }

    @Test
    public void checkForTime_returnsNonDateResponse_whenWrongDate(){
        String expected = "invalidAirportType needs to be a date\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkForTime(createJsonNodeFlight(false,false),"invalidAirportType"));
    }

    @Test
    public void checkTime_returnsEmptyResponse_whenValidDateTimeAttribute(){
        String expected = "";
        //Assertions.assertEquals(expected,predefinedValidator.checkForTime(createJsonNodeFlight(false,false),"validDateAttribute"));
    }

    @Test
    public void checkForPlane_returnsNonExistentField_whenFlightIsMissingFiled(){
        String expected = "The flight doesn't contains any fields with the name invalidKey\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"invalidKey"));
    }

    @Test
    public void checkForPlane_returnsInvalidFlightTypeResponse_whenInvalidFlightType(){
        String expected = "dateTimeType should be a valid plane\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"dateTimeType"));
    }

    @Test
    public void checkForPlane_returnsInvalidFlightStructure_whenPlaneIsMissingId(){
        String expected = "Invalid invalidAirportStructure\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"invalidAirportStructure"));
    }

    @Test
    public void checkForPlane_returnsInvalidPlaneIdType(){
        String expected = "Invalid invalidPlaneId\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"invalidPlaneId"));
    }

    @Test
    public void checkForPlane_returnsNonexistentResponse_whenNonExistentPlane(){
        String expected = "There is  no similar plane\n";
       // Mockito.when(validationRepository.getObject("planes","registrationNumber")).thenReturn(null);
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"validPlane"));
    }

    @Test
    public void checkForPlane_returnsEmptyResponse_whenValidPlane(){
        String expected  = "";
       // Mockito.when(validationRepository.getObject("planes","registrationNumber")).thenReturn(new Document());
        //Assertions.assertEquals(expected,predefinedValidator.checkPlane(createJsonNodeFlight(false,false),"validPlane"));
    }

    @Test
    public void checkForCrew_returnsInvalidFlight_whenFlightMissesRequiredFields(){
        String expected = "The flight should have arrivalTime, departureTime and  a departureAirport\n";
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(false,false,false),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(true,false,false),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(false,true,false),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(false,false,true),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(true,true,false),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(false,true,true),"attribute"));
        //Assertions.assertEquals(expected,predefinedValidator.checkForCrew(crateJsonFlightNode(true,false,true),"attribute"));
    }

    private JsonNode crateJsonFlightNode(Boolean hasArrivalTime,Boolean hasDepartureTime,Boolean hasDeparture){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonFlight = mapper.createObjectNode();
        if(hasArrivalTime){
            jsonFlight.put("arrivalTime","arrivalTime");
        }
        if(hasDepartureTime){
            jsonFlight.put("departureTime","departureTime");
        }
        if(hasDeparture){
            jsonFlight.put("departure","departure");
        }
        return  jsonFlight;
    }

    private JsonNode createJsonNodeFlight(Boolean containsId,Boolean containsLocation) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonFlight = mapper.createObjectNode();
        jsonFlight.put("validDateAttribute","2024-08-27T11:52:53.145Z");
        jsonFlight.put("dateTimeType",14632);
        jsonFlight.put("invalidAirportType", "invalidAirportType");
        ObjectNode invalidAirportId = jsonFlight.putObject("invalidAirportId");
        invalidAirportId.put("icao", 124);
        invalidAirportId.put("location", "location");
        ObjectNode nonExistentAirport = jsonFlight.putObject("nonExistentAirport");
        nonExistentAirport.put("icao", "airportId");
        nonExistentAirport.put("location", "location");
        ObjectNode invalidPlaneId = jsonFlight.putObject("invalidPlaneId");
        invalidPlaneId.put("registrationNumber",34);
        ObjectNode validPlane = jsonFlight.putObject("validPlane");
        validPlane.put("registrationNumber","registrationNumber");
        ObjectNode invalidAirportStructure = jsonFlight.putObject("invalidAirportStructure");
        invalidAirportStructure.put("city", "city");
        if(containsId){
            invalidAirportStructure.put("icao","airportId");
        }
        if(containsLocation){
            invalidAirportStructure.put("location","location");
        }
        return jsonFlight;
    }


}
