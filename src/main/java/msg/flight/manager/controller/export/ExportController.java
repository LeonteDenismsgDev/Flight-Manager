package msg.flight.manager.controller.export;

import msg.flight.manager.persistence.dtos.export.ExportRequest;
import msg.flight.manager.services.export.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("flymanager")
public class ExportController {
    @Autowired
    private ExportService service;

    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody ExportRequest body) throws IOException {
        return service.export(body);
    }
}
