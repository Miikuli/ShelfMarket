package org.example.shelf_market.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
}