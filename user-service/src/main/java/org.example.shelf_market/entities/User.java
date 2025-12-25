package org.example.shelf_market.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "user", schema = "shelf_market")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // ПРОСТО СТРОКА С РОЛЬЮ - без отдельной сущности!
    @Column(name = "role", nullable = false, length = 50)
    private String role = "USER";

    // Конструктор по умолчанию для JPA
    public User() {}

    // Приватный конструктор для Builder'а
    private User(String username, String password, String name,
                 String surname, String phoneNumber, String email, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }

    // Статический метод для получения Builder'а
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Отдельный класс Builder'а
    public static class UserBuilder {
        private String username;
        private String password;
        private String name;
        private String surname;
        private String phoneNumber;
        private String email;
        private String role = "USER";  // Значение по умолчанию

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public UserBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder role(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            // Валидация
            if (username == null || password == null || name == null ||
                    surname == null || phoneNumber == null || email == null) {
                throw new IllegalStateException("All fields are required");
            }
            return new User(username, password, name, surname, phoneNumber, email, role);
        }
    }
}