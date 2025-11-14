package com.example.demo.service;

import com.example.demo.dto.CandidateDto;
import com.example.demo.model.Candidate;
import com.example.demo.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {
    private static final Logger log = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository repo;

    public CandidateService(CandidateRepository repo) {
        this.repo = repo;
    }

    /** Enregistre le candidat et retourne l'id créé. */
    public Long upload(String email, String message, MultipartFile file) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email requis");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("fichier CV manquant");
        }

        // Nom + contentType sécurisés
        final String original = Optional.ofNullable(file.getOriginalFilename()).orElse("cv.pdf");
        final String filename = original.replace("\\", "/").replaceAll("\\s+", "_");
        final String contentType = Optional.ofNullable(file.getContentType())
                .filter(s -> !s.isBlank())
                .orElse("application/pdf");

        try {
            Candidate c = new Candidate();
            c.setEmail(email.trim());
            c.setFilename(filename);
            c.setContentType(contentType);
            c.setMessageText(Optional.ofNullable(message).orElse(""));
            c.setCvData(file.getBytes());
            c.setUploadedAt(Instant.now());

            c = repo.save(c);
            log.info("Upload OK: id={}, email={}, file={}, size={}",
                    c.getId(), c.getEmail(), c.getFilename(), file.getSize());
            return c.getId();
        } catch (IOException e) {
            log.error("Lecture du fichier impossible", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fichier invalide");
        } catch (DataIntegrityViolationException e) {
            log.error("Contrainte base", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "donnée invalide");
        }
    }

    /** Liste au format DTO (sans le BLOB). */
    public List<CandidateDto> list() {
        return repo.findAll().stream()
                .map(c -> new CandidateDto(
                        c.getId(),
                        c.getEmail(),
                        c.getFilename(),
                        c.getContentType(),
                        c.getMessageText(),
                        c.getUploadedAt()
                ))
                .toList();
    }

    /** Récupération brute (pour téléchargement). */
    public Candidate get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
