package com.example.demo.model;

import jakarta.persistence.*;
import java.time.Instant;

// ⬇️ Ajoute ces imports (Hibernate 6)
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "candidate") // table existante
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "message_text", nullable = false, columnDefinition = "text")
    private String messageText = "";

    // ⬇️ IMPORTANT : ne pas mettre @Lob ici
    // On force Hibernate à mapper vers du binaire (BYTEA) et pas vers un OID.
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "cv_data", nullable = false, columnDefinition = "bytea")
    private byte[] cvData;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    // Getters/Setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public String getMessageText() { return messageText; }
    public byte[] getCvData() { return cvData; }
    public Instant getUploadedAt() { return uploadedAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public void setCvData(byte[] cvData) { this.cvData = cvData; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
