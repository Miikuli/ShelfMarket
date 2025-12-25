package org.example.shelf_market.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private UUID userId;

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Геттеры и сеттеры будут созданы автоматически благодаря @Data
    // Но если нужны явно:
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}