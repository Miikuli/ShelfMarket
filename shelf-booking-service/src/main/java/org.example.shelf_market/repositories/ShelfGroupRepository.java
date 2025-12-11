package org.example.shelf_market.repositories;

import org.example.shelf_market.entities.ShelfGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfGroupRepository extends JpaRepository<ShelfGroup, Integer> {
}