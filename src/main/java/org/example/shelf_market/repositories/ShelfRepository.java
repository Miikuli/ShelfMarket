package org.example.shelf_market.repositories;

import org.example.shelf_market.entities.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    List<Shelf> findByShelfGroup_Number(Integer shelfGroupNumber);

    List<Shelf> findByBooked(Boolean booked);
}