package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfGroupDTO;
import org.example.shelf_market.services.ShelfGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelf-groups")
public class ShelfGroupController {

    @Autowired
    private ShelfGroupService shelfGroupService;

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
    public ShelfGroupDTO createShelfGroup(@RequestBody ShelfGroupDTO shelfGroupDTO) {
        return shelfGroupService.createShelfGroup(shelfGroupDTO);
    }

    @PutMapping("/{number}")
    public ResponseEntity<ShelfGroupDTO> updateShelfGroup(
            @PathVariable Integer number,
            @RequestBody ShelfGroupDTO shelfGroupDTO) {
        try {
            ShelfGroupDTO updated = shelfGroupService.updateShelfGroup(number, shelfGroupDTO);
            return ResponseEntity.ok(updated);
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
}