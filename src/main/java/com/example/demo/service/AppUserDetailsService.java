package com.example.demo.service;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repo;

    public AppUserDetailsService(AppUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return User.builder()
                .username(u.getEmail())
                .password(u.getPassword())                 // ← renvoie le hash BCrypt
                .roles(normalizeRole(u.getRole()))         // "ROLE_USER" -> "USER"
                .build();
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) return "USER";
        return role.startsWith("ROLE_") ? role.substring(5) : role;
    }
}
