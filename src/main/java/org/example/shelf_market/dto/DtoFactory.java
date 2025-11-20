package org.example.shelf_market.dto;

import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.entities.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DtoFactory {

    public ShelfDTO createShelfDTO(Shelf shelf) {
        // ИСПРАВЛЕНИЕ: проверяем на null перед вызовом getUser().getId()
        UUID userId = (shelf.getUser() != null) ? shelf.getUser().getId() : null;

        return new ShelfDTO(
                shelf.getId(),
                shelf.getBooked(),
                shelf.getShelfGroup().getNumber(),
                userId  // Может быть null если полка свободна
        );
    }

    public ShelfGroupDTO createShelfGroupDTO(ShelfGroup shelfGroup) {
        return new ShelfGroupDTO(
                shelfGroup.getNumber(),
                shelfGroup.getBooked()
        );
    }

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