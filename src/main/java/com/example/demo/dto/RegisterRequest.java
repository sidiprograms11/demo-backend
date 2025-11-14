package com.example.demo.dto;

public record RegisterRequest(
        String email,
        String password,
        String role
) {}
