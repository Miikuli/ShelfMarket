package org.example.shelf_market.controllers;

import org.example.shelf_market.client.UserServiceClient;
import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.services.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private static final Logger logger = LoggerFactory.getLogger(ShelfController.class);

    @Autowired
    private ShelfService shelfService;

    @Autowired
    private UserServiceClient userServiceClient;

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
    public ResponseEntity<ShelfDTO> createShelf(HttpServletRequest request, @RequestBody ShelfDTO shelfDTO) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create shelves");
        }
        logger.info("Creating shelf with group: {}, booked: {}", shelfDTO.getShelfGroupNumber(), shelfDTO.getBooked());
        try {
            // ВАЛИДАЦИЯ: проверяем обязательные поля
            if (shelfDTO.getShelfGroupNumber() == null) {
                logger.warn("Shelf creation failed: shelfGroupNumber is null");
                return ResponseEntity.badRequest().body(null);
            }

            // Если полка создается как забронированная, должен быть указан пользователь
            if (Boolean.TRUE.equals(shelfDTO.getBooked()) && shelfDTO.getUserId() == null) {
                logger.warn("Shelf creation failed: booked=true but userId is null");
                return ResponseEntity.badRequest().body(null);
            }

            ShelfDTO created = shelfService.createShelf(shelfDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            logger.error("Error creating shelf: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelfDTO> updateShelf(
            @PathVariable Integer id,
            HttpServletRequest request,
            @RequestBody ShelfDTO shelfDTO) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update shelves");
        }
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