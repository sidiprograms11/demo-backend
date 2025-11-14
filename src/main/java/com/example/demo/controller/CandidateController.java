package com.example.demo.controller;

import com.example.demo.dto.CandidateDto;
import com.example.demo.model.Candidate;
import com.example.demo.service.CandidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*") // ok pour le dev Angular
public class CandidateController {
    private static final Logger log = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam String email,
                                    @RequestParam(defaultValue = "") String message,
                                    @RequestParam("cv") MultipartFile file) {
        try {
            Long id = service.upload(email, message, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (IllegalArgumentException e) {
            log.warn("Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur serveur côté upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur");
        }

    }


    @GetMapping
    public List<CandidateDto> list() {
        return service.list();
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Candidate c = service.get(id);
        if (c == null) return ResponseEntity.notFound().build();

        HttpHeaders h = new HttpHeaders();
        // si contentType est vide on force PDF
        String ct = (c.getContentType() == null || c.getContentType().isBlank())
                ? MediaType.APPLICATION_PDF_VALUE : c.getContentType();
        h.setContentType(MediaType.parseMediaType(ct));
        h.setContentDisposition(ContentDisposition.attachment().filename(c.getFilename()).build());
        return new ResponseEntity<>(c.getCvData(), h, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
