package org.example.shelf_market.repositories;

import org.example.shelf_market.entities.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    @Query("SELECT COUNT(s) FROM Shelf s WHERE s.user.id = :userId AND s.booked = true")
    long countByUserIdAndBookedTrue(@Param("userId") UUID userId);

    @Query("SELECT s FROM Shelf s WHERE s.user.id = :userId AND s.booked = true")
    List<Shelf> findBookedShelvesByUserId(@Param("userId") UUID userId);

    boolean existsById(Integer id);

    List<Shelf> findByShelfGroup_NumberAndBooked(Integer shelfGroupNumber, Boolean booked);


    List<Shelf> findByShelfGroup_Number(Integer shelfGroupNumber);

    List<Shelf> findByBooked(Boolean booked);
}