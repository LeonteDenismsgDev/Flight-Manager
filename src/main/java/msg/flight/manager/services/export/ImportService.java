package msg.flight.manager.services.export;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.AirportRepository;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.services.companies.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImportService {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    PlaneRepository planeRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    UserRepository userRepository;

    public void importData(String type, String fileContents){
        List<String> lines = Arrays.stream(fileContents.split("\n")).toList();
        if(type.equals("Planes")){
            List<DBPlane> parsedPlanes=new ArrayList<>();
            for(String line : lines){
                parsedPlanes.add(parsePlane(line));
            }
            for(DBPlane plane : parsedPlanes){
                if(this.planeRepository.get(plane.getRegistrationNumber()) == null){
                    this.planeRepository.save(plane);
                }
                else{
                    this.planeRepository.update(plane.getRegistrationNumber(),plane);
                }
            }
        }
        else if(type.equals("Airports")){
            List<DBAirport> parsedAirports = new ArrayList<>();
            for(String line:lines){
                parsedAirports.add(parseAirport(line));
            }
            for(DBAirport airport:parsedAirports){
                if(this.airportRepository.get(airport.getIcao()) == null){
                    this.airportRepository.save(airport);
                }
                else{
                    this.airportRepository.update(airport.getIcao(),airport);
                }
            }
        }
        else if(type.equals("Companies")){
            List<DBCompany> parsedCompanies = new ArrayList<>();
            for(String line :lines){
                parsedCompanies.add(parseCompany(line));
            }
            for(DBCompany company: parsedCompanies){
                if(this.companyRepository.get(company.getName()) == null){
                    this.companyRepository.save(company);
                }
                else{
                    this.companyRepository.update(company.getName(),company);
                }
            }
        }
        else if(type.equals("Users")){
            List<DBUser> parsedUsers=new ArrayList<>();
            for(String line: lines){
                parsedUsers.add(parseUser(line));
            }
            for(DBUser user:parsedUsers){
                if(userRepository.findDataByUsername(user.getUsername())==null){
                    userRepository.save(user);
                }
            }
        }
    }

    public DBPlane parsePlane(String row){
    List<String> values= Arrays.stream(row.split(",")).toList();
    DBCompany tempDBComp = this.companyRepository.get(values.get(3));
    Company tempComp = Company.builder()
            .name(tempDBComp.getName())
            .contactData(tempDBComp.getContactData())
            .fleet(tempDBComp.getFleet())
            .build();
    return DBPlane.builder()
            .registrationNumber(values.get(0))
            .manufacturer(values.get(1))
            .model(values.get(2))
            .company(tempComp)
            .manufactureYear((int) Integer.parseInt(values.get(4)))
            .range((int) Integer.parseInt(values.get(5)))
            .length((float) Float.parseFloat(values.get(6)))
            .height((float) Float.parseFloat(values.get(7)))
            .wingspan((float) Float.parseFloat(values.get(8)))
            .cruisingSpeed((int) Integer.parseInt(values.get(9)))
            .build();
    }

    public DBAirport parseAirport(String row){
        List<String> values= Arrays.stream(row.split(",")).toList();
        Map<String,String> contacts = new HashMap<>();
        for(int i = 4; i < values.size(); i++){
            String key= Arrays.stream(values.get(i).split(":")).toList().get(0);
            String value= Arrays.stream(values.get(i).split(":")).toList().get(1);
            contacts.put(key,value);
        }
        return DBAirport.builder()
                .airportName(values.get(0))
                .icao(values.get(1))
                .iata(values.get(2))
                .location(values.get(3))
                .contactData(contacts)
                .build();
    }

    public DBCompany parseCompany(String row){
        List<String> values = Arrays.stream(row.split(",")).toList();
        Map<String,String> contacts = new HashMap<>();
        for(int i = 2; i < values.size();i++){
            String key= Arrays.stream(values.get(i).split(":")).toList().get(0);
            String value= Arrays.stream(values.get(i).split(":")).toList().get(1);
            contacts.put(key,value);
        }
        return DBCompany.builder()
                .name(values.get(0))
                .fleet((int) Integer.parseInt(values.get(1)))
                .contactData(contacts)
                .build();
    }

    public DBUser parseUser(String row){
        List<String> values = Arrays.stream(row.split(",")).toList();
        Map<String,String> contacts = new HashMap<>();
        for(int i = 7; i < values.size();i++){
            String key= Arrays.stream(values.get(i).split(":")).toList().get(0);
            String value= Arrays.stream(values.get(i).split(":")).toList().get(1);
            contacts.put(key,value);
        }
        return DBUser.builder()
                .username(values.get(0))
                .firstName(values.get(1))
                .lastName(values.get(2))
                .role(values.get(3))
                .company(values.get(4))
                .address(values.get(5))
                .enabled((boolean) Boolean.parseBoolean(values.get(6)))
                .contactData(contacts)
                .build();
    }
}
