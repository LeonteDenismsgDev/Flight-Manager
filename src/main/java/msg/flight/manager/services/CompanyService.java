package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository repository;

    SecurityUserUtil securityUser = new SecurityUserUtil();

    public ResponseEntity<?> save(Company company){
        DBCompany dbCompany = DBCompany.builder().name(company.getName().trim())
                .fleet(company.getFleet())
                .contactData(company.getContactData())
                .build();
        if(this.repository.get(company.getName())!=null){
            return new ResponseEntity<>("Another company with the same name exists already",HttpStatusCode.valueOf(400));
        }
        if(this.repository.save(dbCompany) == null){
            return new ResponseEntity<>("Unable to create the company", HttpStatusCode.valueOf(400));
        }
        return new ResponseEntity<>("Company created",HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> viewAll(){
        return new ResponseEntity<>(this.repository.getAll(), HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> delete(String name){
        if(this.repository.remove(name)){
            return new ResponseEntity<>("Company deleted",HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>("Unable to delete company",HttpStatusCode.valueOf(400));
    }

    public ResponseEntity<String> update(String name, Company company){
        SecurityUser loggedUser = securityUser.getLoggedUser();
        if(!loggedUser.getCompany().equals(name)){
            return new ResponseEntity<>("You dont have the permission to edit this company",HttpStatusCode.valueOf(403));
        }
        DBCompany dbCompany = DBCompany.builder().name(company.getName().trim())
                .fleet(company.getFleet())
                .contactData(company.getContactData())
                .build();
        if(this.repository.update(name,dbCompany)){
            return new ResponseEntity<>("Company updated",HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>("Unable to update company",HttpStatusCode.valueOf(400));
    }

    public ResponseEntity<?> viewOne(String name){
        SecurityUser loggedUser = securityUser.getLoggedUser();
        if(!loggedUser.getCompany().equals(name) ){
            return new ResponseEntity<String>("You dont have the permission to view this company",HttpStatusCode.valueOf(403));
        }
        DBCompany found = this.repository.get(name);
        if(found == null){
            return new ResponseEntity<>("Unable to find the company",HttpStatusCode.valueOf(404));
        }
        return new ResponseEntity<DBCompany>(found, HttpStatusCode.valueOf(200));
    }
}
