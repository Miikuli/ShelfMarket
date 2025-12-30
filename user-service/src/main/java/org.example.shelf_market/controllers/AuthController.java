package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.LoginRequest;
import org.example.shelf_market.dto.LoginResponse;
import org.example.shelf_market.dto.TokenValidationResponse;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Добавляем Logger
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login request for user: {}", loginRequest.getUsername());

        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            logger.warn("User not found: {}", loginRequest.getUsername());
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid password");
        }

        String token = jwtUtils.generateTokenFromUsername(user.getUsername());

        // Создаем LoginResponse с нужными параметрами
        LoginResponse response = new LoginResponse(token, user.getUsername());
        // Устанавливаем роль и userId через конструктор или методы
        response.setRole(user.getRole() != null ? user.getRole() : "USER");
        response.setUserId(user.getId());

        logger.info("Login successful for user: {}, role: {}, id: {}",
                user.getUsername(), user.getRole(), user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("Token validation request");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String token = authHeader.substring(7);

        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                TokenValidationResponse response = new TokenValidationResponse(username, user.getRole(), user.getId());
                logger.info("Token valid for user: {}, role: {}, id: {}", username, user.getRole(), user.getId());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("User not found for token: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
        }

        logger.warn("Invalid token");
        return ResponseEntity.badRequest().body("Invalid token");
    }
}