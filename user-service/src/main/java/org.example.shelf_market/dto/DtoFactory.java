package org.example.shelf_market.dto;

import org.example.shelf_market.entities.User;
import org.springframework.stereotype.Component;

@Component
public class DtoFactory {

    public UserDTO createUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getPhoneNumber(),
                user.getEmail()
        );
    }
}