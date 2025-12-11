package org.example.shelf_market.dto;

import lombok.Value;

import java.util.UUID;


@Value
public class UserDTO {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
}