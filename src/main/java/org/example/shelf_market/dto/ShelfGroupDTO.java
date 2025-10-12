package org.example.shelf_market.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelfGroupDTO {
    private Integer number;
    private Boolean booked;

    public ShelfGroupDTO() {}

    public ShelfGroupDTO(Integer number, Boolean booked) {
        this.number = number;
        this.booked = booked;
    }
}