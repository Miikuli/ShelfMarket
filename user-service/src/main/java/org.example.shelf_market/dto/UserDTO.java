package org.example.shelf_market.dto;

import lombok.Value;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor  // Конструктор без параметров ОБЯЗАТЕЛЕН!
@AllArgsConstructor // Конструктор со всеми параметрами
@JsonInclude(JsonInclude.Include.NON_NULL) // Не включать null значения в JSON
public class UserDTO {
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+7|8)\\d{10}$",
             message = "Phone number must start with +7 or 8 and contain exactly 10 digits after the prefix")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String role;

    // Дополнительный конструктор для обратной совместимости
    public UserDTO(UUID id, String username, String password, String name,
                   String surname, String phoneNumber, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = "USER"; // значение по умолчанию
    }
}