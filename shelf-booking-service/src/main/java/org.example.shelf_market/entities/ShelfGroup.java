package org.example.shelf_market.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shelf_group", schema = "shelf_market")
@Getter
@Setter
public class ShelfGroup {
    @Id
    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "booked", nullable = false)
    private Boolean booked;
}