package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthController(AppUserRepository users,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }
        AppUser u = new AppUser();
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password())); // 🔐 hash BCrypt (sans {bcrypt})
        u.setRole(req.role() == null || req.role().isBlank() ? "ROLE_USER" : req.role());
        users.save(u);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var tokenAuth = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        authManager.authenticate(tokenAuth); // lève BadCredentialsException si KO

        String token = jwt.generateToken(req.email()); // ou avec UserDetails si tu préfères
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
