package msg.flight.manager.controller.company;

import jakarta.validation.Valid;
import msg.flight.manager.persistence.dtos.company.Company;
import msg.flight.manager.persistence.models.company.DBCompany;
import msg.flight.manager.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("flymanager/company")
public class CompanyController {
    @Autowired
    CompanyService service;

    @GetMapping("/view/all")
    public ResponseEntity<?> getAll(){
        return this.service.viewAll();
    }

    @PostMapping("/create")
    public ResponseEntity<?> save(@Valid @RequestBody(required = true)Company company){
        return this.service.save(company);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestParam(required = true)String name){
        return this.service.delete(name);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody(required = true) Company company, @Valid @RequestParam(required = true) String name){
        return this.service.update(name, company);
    }

    @GetMapping("/view")
    public ResponseEntity<?> getOne(@Valid @RequestParam(required = true) String name){
        return this.service.viewOne(name);
    }
}
