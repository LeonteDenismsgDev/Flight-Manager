package msg.flight.manager.services;

import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.dtos.company.UpdateCompanyDTO;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.persistence.repositories.CompanyRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.companies.CompanyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {
    @Mock
    private CompanyRepository repository;

    @InjectMocks
    private CompanyService service;

    @Before
    public void setUp(){
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(new SecurityUser("0denLad","",true,"ADMINISTRATOR_ROLE","Wizz Air"));
        service = new CompanyService();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void viewOne_DBCompany_whenUserCompanyIsCorrect(){
        Mockito.when(this.repository.get("Wizz Air")).thenReturn(new DBCompany("Wizz Air",10,new HashMap<String,String>()));
        ResponseEntity<?> response = service.viewOne("Wizz Air");
        assert(response.getBody().equals(new DBCompany("Wizz Air",10,new HashMap<String,String>())));
    }

    @Test
    public void viewOne_errorMessage_whenUserCompanyIsNotCorrect(){
        ResponseEntity<?> response = service.viewOne("Lufthansa");
        assert(response.getBody().equals("You dont have the permission to view this company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(403)));
    }

    @Test
    public void viewOne_errorMessage_whenNoCompanyIsFound(){
        Mockito.when(this.repository.get("Wizz Air")).thenReturn(null);
        ResponseEntity<?> response = service.viewOne("Wizz Air");
        assert(response.getBody().equals("Unable to find the company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(404)));
    }

//    @Test
//    public void viewAll_CompanyList_whenUserIsAdmin(){
//        ArrayList<DBCompany> results = new ArrayList<>();
//        results.add(new DBCompany("Wizz Air",10,new HashMap<String,String>()));
//        Mockito.when(this.repository.getAll()).thenReturn(results);
//        ResponseEntity<?> response = service.viewAll();
//        assert(response.getBody().equals(results));
//    }

    @Test
    public void delete_successMessage_whenUserIsAdmin(){
        Mockito.when(this.repository.remove("Wizz Air")).thenReturn(true);
        ResponseEntity<?> response = service.delete("Wizz Air");
        assert(response.getBody().equals("Company deleted"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

    @Test
    public void delete_errorMessage_whenUserIsAdminButDeleteFailed(){
        Mockito.when(this.repository.remove("Wizz Air")).thenReturn(false);
        ResponseEntity<?> response = service.delete("Wizz Air");
        assert(response.getBody().equals("Unable to delete company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(400)));
    }

    @Test
    public void save_errorMessage_whenCompanyNameIsPresentAlready(){
        Mockito.when(this.repository.get("Wizz Air")).thenReturn(new DBCompany("Wizz Air",10,new HashMap<String,String>()));
        ResponseEntity<?> response = service.save(new Company("Wizz Air",10,new HashMap<String,String>(),0));
        assert(response.getBody().equals("Another company with the same name exists already"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(400)));
    }

    @Test
    public void save_errorMessage_whenRepositorySaveOperationFails(){
        Mockito.when(this.repository.get("Wizz Air")).thenReturn(null);
        Mockito.when(this.repository.save(new DBCompany("Wizz Air",10,new HashMap<String,String>()))).thenReturn(null);
        ResponseEntity<?> response = service.save(new Company("Wizz Air",10,new HashMap<String,String>(),0));
        assert(response.getBody().equals("Unable to create the company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(400)));
    }
    @Test
    public void save_successMessage_whenRepositorySaveOperationSucceeds(){
        Mockito.when(this.repository.get("Wizz Air")).thenReturn(null);
        Mockito.when(this.repository.save(new DBCompany("Wizz Air",10,new HashMap<String,String>())))
                .thenReturn(new DBCompany("Wizz Air",10,new HashMap<String,String>()));
        ResponseEntity<?> response = service.save(new Company("Wizz Air",10,new HashMap<String,String>(),0));
        assert(response.getBody().equals("Company created"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }

    @Test
    public void update_errorMessage_whenCompanyIsNotEqual(){
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(new SecurityUser("0denLad","",true,"COMPANY_MANAGER","Wizz Air"));
        ResponseEntity<?> response = service.update("Lufthansa",new UpdateCompanyDTO("Wizz Air",new HashMap<String,String>()));
        assert(response.getBody().equals("You dont have the permission to edit this company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(403)));
    }

    @Test
    public void update_errorMessage_whenIsEqualButRepositoryOperationFailed(){
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(new SecurityUser("0denLad","",true,"COMPANY_MANAGER","Wizz Air"));
//        Mockito.when(this.repository.update("Wizz Air",new DBCompany("Wizz Air",10,new HashMap<String,String>())))
//                .thenReturn(false);
        ResponseEntity<?> response = service.update("Wizz Air",new UpdateCompanyDTO("Wizz Air",new HashMap<String,String>()));
        assert(response.getBody().equals("Unable to update company"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(400)));
    }

    @Test
    public void update_successMessage_whenCompanyIsEqualAndRepositoryOperationSucceded(){
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .thenReturn(new SecurityUser("0denLad","",true,"COMPANY_MANAGER","Wizz Air"));
        Mockito.when(this.repository.update("Wizz Air",new DBCompany("Wizz Air",10,new HashMap<String,String>())))
                .thenReturn(true);
        ResponseEntity<?> response = service.update("Wizz Air",new UpdateCompanyDTO("Wizz Air",new HashMap<String,String>()));
        assert(response.getBody().equals("Company updated"));
        assert(response.getStatusCode().equals(HttpStatusCode.valueOf(200)));
    }
}
