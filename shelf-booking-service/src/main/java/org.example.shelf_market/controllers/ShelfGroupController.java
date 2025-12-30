package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfGroupDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.exceptions.GroupHasBookedShelvesException;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.services.ShelfGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import org.example.shelf_market.client.UserServiceClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/shelf-groups")
public class ShelfGroupController {

    @Autowired
    private ShelfGroupService shelfGroupService;

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @GetMapping
    public List<ShelfGroupDTO> getAllShelfGroups() {
        return shelfGroupService.getAllShelfGroups();
    }

    @GetMapping("/{number}")
    public ResponseEntity<ShelfGroupDTO> getShelfGroupById(@PathVariable Integer number) {
        try {
            ShelfGroupDTO shelfGroup = shelfGroupService.getShelfGroupById(number);
            return ResponseEntity.ok(shelfGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ShelfGroupDTO createShelfGroup(HttpServletRequest request, @RequestBody ShelfGroupDTO shelfGroupDTO) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create shelf groups");
        }
        return shelfGroupService.createShelfGroup(shelfGroupDTO);
    }

    @PutMapping("/{number}")
    public ResponseEntity<ShelfGroupDTO> updateShelfGroup(
            @PathVariable Integer number,
            @RequestBody ShelfGroupDTO shelfGroupDTO) {
        try {
            ShelfGroupDTO updated = shelfGroupService.updateShelfGroup(number, shelfGroupDTO);
            return ResponseEntity.ok(updated);
        } catch (GroupHasBookedShelvesException e) {
            // Специальный статус 423 Locked - группа заблокирована забронированными полками
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteShelfGroup(@PathVariable Integer number) {
        try {
            shelfGroupService.deleteShelfGroup(number);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{number}/update-status")
    public ResponseEntity<Void> updateGroupStatus(@PathVariable Integer number) {
        try {
            shelfGroupService.updateGroupBookingStatus(number);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update-all-statuses")
    public ResponseEntity<Void> updateAllGroupsStatuses() {
        try {
            List<ShelfGroupDTO> allGroups = shelfGroupService.getAllShelfGroups();
            for (ShelfGroupDTO group : allGroups) {
                shelfGroupService.updateGroupBookingStatus(group.getNumber());
            }
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Получить информацию о забронированных полках в группе
     */
    @GetMapping("/{number}/booked-shelves")
    public ResponseEntity<Long> getBookedShelvesCount(@PathVariable Integer number) {
        try {
            List<Shelf> bookedShelves = shelfRepository.findByShelfGroup_NumberAndBooked(number, true);
            return ResponseEntity.ok((long) bookedShelves.size());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получить информацию можно ли освободить группу
     */
    @GetMapping("/{number}/can-be-freed")
    public ResponseEntity<Boolean> canGroupBeFreed(@PathVariable Integer number) {
        try {
            boolean canBeFreed = shelfGroupService.canSetGroupAvailable(number);
            return ResponseEntity.ok(canBeFreed);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}