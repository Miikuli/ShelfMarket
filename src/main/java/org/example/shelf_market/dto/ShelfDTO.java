package org.example.shelf_market.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class ShelfDTO {
    private Integer id;
    private Boolean booked;
    private Integer shelfGroupNumber;
    private UUID userId;

    // Конструкторы
    public ShelfDTO() {}

    public ShelfDTO(Integer id, Boolean booked, Integer shelfGroupNumber, UUID userId) {
        this.id = id;
        this.booked = booked;
        this.shelfGroupNumber = shelfGroupNumber;
        this.userId = userId;
    }
}