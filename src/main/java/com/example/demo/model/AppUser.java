package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email unique
    @Column(nullable = false, unique = true)
    private String email;

    // ⚠️ mappe la colonne 'password_hash' (hash BCrypt 60 char, sans {bcrypt})
    @Column(name = "password_hash", nullable = false, length = 100)
    private String password;

    // on stocke 'ROLE_USER' / 'ROLE_ADMIN' en DB
    @Column(nullable = false, length = 50)
    private String role = "ROLE_USER";

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }    // renvoie le hash
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
