package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.services.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    @Autowired
    private ShelfService shelfService;

    @GetMapping
    public List<ShelfDTO> getAllShelves() {
        return shelfService.getAllShelves();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelfDTO> getShelfById(@PathVariable Integer id) {
        try {
            ShelfDTO shelf = shelfService.getShelfById(id);
            return ResponseEntity.ok(shelf);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/group/{shelfGroupNumber}")
    public List<ShelfDTO> getShelvesByGroup(@PathVariable Integer shelfGroupNumber) {
        return shelfService.getShelvesByGroup(shelfGroupNumber);
    }

    @PostMapping
    public ShelfDTO createShelf(@RequestBody ShelfDTO shelfDTO) {
        return shelfService.createShelf(shelfDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelfDTO> updateShelf(
            @PathVariable Integer id,
            @RequestBody ShelfDTO shelfDTO) {
        try {
            ShelfDTO updated = shelfService.updateShelf(id, shelfDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Integer id) {
        try {
            shelfService.deleteShelf(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}