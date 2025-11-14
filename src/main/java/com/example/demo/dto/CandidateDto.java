package com.example.demo.dto;

import java.time.Instant;

public record CandidateDto(
        Long id,
        String email,
        String filename,
        String contentType,
        String messageText,
        Instant uploadedAt
) {}
