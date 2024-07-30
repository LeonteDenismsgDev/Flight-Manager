package msg.flight.manager.services.companies;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.dtos.company.UpdateCompanyDTO;
import msg.flight.manager.persistence.enums.Role;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.models.user.DBUser;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.persistence.repositories.UserRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository repository;

    @Autowired
    UserRepository userRepository;

    private SecurityUserUtil securityUser = new SecurityUserUtil();

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
        return new ResponseEntity<>(this.repository.getAll().stream().
                map(a->Company.builder()
                        .name(a.getName())
                        .fleet(a.getFleet())
                        .contactData(a.getContactData())
                        .crews(this.getEmployeesNumber(a.getName()))
                        .build()),
                HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<?> delete(String name){
        if(this.repository.remove(name)){
            return new ResponseEntity<>("Company deleted",HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>("Unable to delete company",HttpStatusCode.valueOf(400));
    }

    public ResponseEntity<String> update(String name, UpdateCompanyDTO company){
        SecurityUser loggedUser = securityUser.getLoggedUser();
        if(!loggedUser.getCompany().equals(name)){
            return new ResponseEntity<>("You dont have the permission to edit this company",HttpStatusCode.valueOf(403));
        }
        DBCompany dbCompany = DBCompany.builder().name(company.getName().trim())
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


    public int getEmployeesNumber(String name){
        if(repository.get(name) == null){
            return -1;
        }
        int crews = 0;
        List<DBUser> users = this.userRepository.getAll();
        for(DBUser user : users){
            if(user.getCompany().equals(name)){
                crews++;
            }
        }
        return crews;
    }

    public ResponseEntity<?> getCurrent(){
        SecurityUser user = this.securityUser.getLoggedUser();
        return this.viewOne(user.getCompany());
    }
}
