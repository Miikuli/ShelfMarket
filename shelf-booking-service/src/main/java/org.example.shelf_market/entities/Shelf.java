package org.example.shelf_market.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "shelf", schema ="shelf_market")
public class Shelf {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "booked", nullable = false)
    private Boolean booked = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shelf_group_number", nullable = false)
    private ShelfGroup shelfGroup;

    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    public Shelf() {
        this.booked = false;
    }
}
