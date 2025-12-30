package org.example.shelf_market.dto;

import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DtoFactory {

    public ShelfDTO createShelfDTO(Shelf shelf) {
        // ИЗМЕНЕНИЕ: теперь получаем userId напрямую из поля shelf.getUserId()
        // (а не через shelf.getUser().getId())
        UUID userId = shelf.getUserId();  // ← Просто берём UUID из поля

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
}