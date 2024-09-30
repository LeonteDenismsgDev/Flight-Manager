package msg.flight.manager.services.export;

import msg.flight.manager.persistence.dtos.export.*;
import msg.flight.manager.persistence.models.airport.DBAirport;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.models.plane.DBPlane;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.AirportRepository;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.persistence.repositories.PlaneRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExportService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    AirportRepository airportRepository;
    @Autowired
    PlaneRepository planeRepository;

    private String DBUser2CSV(DBUser user) {
        String ret =  user.getUsername()+","+
                user.getFirstName()+","+
                user.getLastName()+","+
                user.getRole()+","+
                user.getCompany()+","+
                user.getAddress()+","+
                user.getEnabled();
        if(!user.getContactData().isEmpty()) {
            ret+=",";
            for (Map.Entry<String, String> entry : user.getContactData().entrySet()) {
                ret += entry.getKey() + ":" + entry.getValue() + ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    private String DBCompany2CSV(DBCompany company){
        String ret = company.getName()+","+
                company.getFleet();
        if(!company.getContactData().isEmpty()) {
            ret+=",";
            for (Map.Entry<String, String> entry : company.getContactData().entrySet()) {
                ret += entry.getKey() + ":" + entry.getValue() + ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    private String DBAirport2CSV(DBAirport airport){
        StringBuilder builder = new StringBuilder();
        builder.append(airport.getAirportName()).append(",")
                .append(airport.getIcao()).append(",")
                .append(airport.getIata()).append(",")
                .append(airport.getLocation());
        if(!airport.getContactData().isEmpty()) {
            builder.append(",");
            for (Map.Entry<String, String> entry : airport.getContactData().entrySet()) {
                builder.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        return builder.toString();
    }

    private String DBPlane2CSV(DBPlane plane){
        StringBuilder builder = new StringBuilder();
        builder.append(plane.getRegistrationNumber()).append(",")
                .append(plane.getManufacturer()).append(",")
                .append(plane.getModel()).append(",")
                .append(plane.getCompany().getName()).append(",")
                .append(plane.getManufactureYear()).append(",")
                .append(plane.getRange()).append(",")
                .append(plane.getLength()).append(",")
                .append(plane.getHeight()).append(",")
                .append(plane.getWingspan()).append(",")
                .append(plane.getCruisingSpeed());
        return builder.toString();
    }

    public ResponseEntity<?> export(ExportRequest request) throws IOException {
        if(request.getDataType().equals("Users")) {
            List<DBUser> allUsers = this.userRepository.getAll();
            if (request.getSelection().equals("Filter")) {
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                UserExportRequest user_request = UserExportRequest.builder()
                        .company((String) mapped.get("company"))
                        .role((List<String>) mapped.get("role"))
                        .build();
                if (!user_request.company.isBlank()) {
                    allUsers = allUsers.stream().filter((d) -> d.getCompany().equals(user_request.getCompany())).toList();
                }
                if (!user_request.getRole().isEmpty()) {
                    allUsers = allUsers.stream().filter((d) -> user_request.getRole().contains(d.getRole())).toList();
                }
                if(!allUsers.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBUser user : allUsers){
                        csvBuild.append(DBUser2CSV(user)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
            else if(request.getSelection().equals("Ranged")){
                RangedExportRequest rangedExportRequest= new RangedExportRequest();
                List<DBUser> res=new ArrayList<>();
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                rangedExportRequest = RangedExportRequest.builder()
                        .minRange((int)mapped.get("minRange"))
                        .maxRange((int)mapped.get("maxRange"))
                        .build();
                for(int i = rangedExportRequest.getMinRange(); i <= rangedExportRequest.getMaxRange(); i++){
                    res.add(allUsers.get(i));
                }
                if(!res.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBUser user : res){
                        csvBuild.append(DBUser2CSV(user)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }else{
                if(!allUsers.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBUser user : allUsers){
                        csvBuild.append(DBUser2CSV(user)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
        }
        else if(request.getDataType().equals("Companies")){
            List<DBCompany> allCompanies = this.companyRepository.getAll();
            if (request.getSelection().equals("Filter")) {
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                CompanyExportRequest company_request = CompanyExportRequest.builder()
                        .minCrew((int)mapped.get("minCrew"))
                        .maxCrew((int)mapped.get("maxCrew"))
                        .minFleet((int)mapped.get("minFleet"))
                        .maxFleet((int)mapped.get("maxFleet"))
                        .build();
                allCompanies = allCompanies.stream().filter((c)->{return c.getFleet()>=company_request.getMinFleet()&&
                                                            c.getFleet()<=company_request.getMaxFleet();
                }).toList();
                if(!allCompanies.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBCompany company : allCompanies){
                        csvBuild.append(DBCompany2CSV(company)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
            else if(request.getSelection().equals("Ranged")){
                RangedExportRequest rangedExportRequest= new RangedExportRequest();
                List<DBCompany> res=new ArrayList<>();
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                rangedExportRequest = RangedExportRequest.builder()
                        .minRange((int)mapped.get("minRange"))
                        .maxRange((int)mapped.get("maxRange"))
                        .build();
                for(int i = rangedExportRequest.getMinRange(); i <= rangedExportRequest.getMaxRange(); i++){
                    res.add(allCompanies.get(i));
                }
                if(!res.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBCompany company : res){
                        csvBuild.append(DBCompany2CSV(company)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }else{
                if(!allCompanies.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBCompany company : allCompanies){
                        csvBuild.append(DBCompany2CSV(company)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
        }
        else if(request.getDataType().equals("Airports")){
            List<DBAirport> allAirports = this.airportRepository.get();
            if(request.getSelection().equals("Ranged")){
                RangedExportRequest rangedExportRequest= new RangedExportRequest();
                List<DBAirport> res=new ArrayList<>();
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                rangedExportRequest = RangedExportRequest.builder()
                        .minRange((int)mapped.get("minRange"))
                        .maxRange((int)mapped.get("maxRange"))
                        .build();
                for(int i = rangedExportRequest.getMinRange(); i <= rangedExportRequest.getMaxRange(); i++){
                    res.add(allAirports.get(i));
                }
                if(!res.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBAirport airport : res){
                        csvBuild.append(DBAirport2CSV(airport)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }else{
                if(!allAirports.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBAirport airport : allAirports){
                        csvBuild.append(DBAirport2CSV(airport)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
        }
        else if(request.getDataType().equals("Planes")){
            List<DBPlane> allPlanes = this.planeRepository.get();
            if (request.getSelection().equals("Filter")) {
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                PlaneExportRequest plane_request = PlaneExportRequest.builder()
                        .model((String)mapped.get("model"))
                        .manufacturer((String)mapped.get("manufacturer"))
                        .company((String)mapped.get("company"))
                        .minYear((int)mapped.get("minYear"))
                        .maxYear((int)mapped.get("maxYear"))
                        .build();
                if(!plane_request.getModel().isBlank()){
                    allPlanes = allPlanes.stream().filter(p->{return p.getModel().contains(plane_request.getModel());}).toList();
                }
                if(!plane_request.getManufacturer().isBlank()){
                    allPlanes = allPlanes.stream().filter(p->{return p.getManufacturer().equals(plane_request.getManufacturer());}).toList();
                }
                if(!plane_request.getCompany().isBlank()){
                    allPlanes = allPlanes.stream().filter(p->{return p.getCompany().getName().equals(plane_request.getCompany());}).toList();
                }
                allPlanes = allPlanes.stream().filter(p->{return p.getManufactureYear()>= plane_request.getMinYear() && p.getManufactureYear()<= plane_request.getMaxYear();}).toList();
                if(!allPlanes.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBPlane plane : allPlanes){
                        csvBuild.append(DBPlane2CSV(plane)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
            else if(request.getSelection().equals("Ranged")){
                RangedExportRequest rangedExportRequest= new RangedExportRequest();
                List<DBPlane> res=new ArrayList<>();
                LinkedHashMap<String, Object> mapped = (LinkedHashMap<String, Object>) request.getRequest();
                rangedExportRequest = RangedExportRequest.builder()
                        .minRange((int)mapped.get("minRange"))
                        .maxRange((int)mapped.get("maxRange"))
                        .build();
                for(int i = rangedExportRequest.getMinRange(); i <= rangedExportRequest.getMaxRange(); i++){
                    res.add(allPlanes.get(i));
                }
                if(!res.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBPlane plane : res){
                        csvBuild.append(DBPlane2CSV(plane)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }else{
                if(!allPlanes.isEmpty()){
                    StringBuilder csvBuild = new StringBuilder();
                    for(DBPlane plane : allPlanes){
                        csvBuild.append(DBPlane2CSV(plane)).append("\n");
                    }
                    String csvRes = csvBuild.toString();
                    Path tempFile = Files.createTempFile("export",".csv");
                    Files.write(tempFile,csvRes.getBytes());
                    Resource resource = new UrlResource(tempFile.toUri());
                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.txt");
                    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                    // Return the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(Files.size(tempFile))
                            .body(resource);
                }
                return new ResponseEntity<>("Nothing found",HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Nothing found", HttpStatus.NOT_FOUND);
    }
}
