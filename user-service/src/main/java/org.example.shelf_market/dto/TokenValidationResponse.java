package org.example.shelf_market.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TokenValidationResponse {
    private String username;
    private String role;
    private UUID userId;

    public TokenValidationResponse(String username, String role, UUID userId) {
        this.username = username;
        this.role = role;
        this.userId = userId;
    }
}