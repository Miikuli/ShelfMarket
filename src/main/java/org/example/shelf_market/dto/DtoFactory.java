package org.example.shelf_market.dto;

import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.dto.ShelfGroupDTO;
import org.example.shelf_market.entities.User;
import org.springframework.stereotype.Component;

@Component
public class DtoFactory {

    public ShelfDTO createShelfDTO(Shelf shelf) {
        return new ShelfDTO(
                shelf.getId(),
                shelf.getBooked(),
                shelf.getShelfGroup().getNumber(),
                shelf.getUser().getId()
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