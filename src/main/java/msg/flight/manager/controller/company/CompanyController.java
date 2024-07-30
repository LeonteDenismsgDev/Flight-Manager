package msg.flight.manager.controller.company;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.services.companies.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("flymanager/company")
public class CompanyController {
    @Autowired
    private CompanyService service;

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @GetMapping("/view/all")
    public ResponseEntity<?> getAll(){
        return this.service.viewAll();
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid @RequestBody(required = true)Company company){
        return this.service.save(company);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR_ROLE')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestParam(required = true)String name){
        return this.service.delete(name);
    }

    @PreAuthorize("hasAuthority('COMPANY_MANAGER_ROLE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody(required = true) Company company, @Valid @RequestParam(required = true) String name){
        return this.service.update(name, company);
    }

    @GetMapping("/view")
    public ResponseEntity<?> getOne(@Valid @RequestParam(required = true) String name){
        return this.service.viewOne(name);
    }
}
