package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.facade.BookingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingFacade bookingFacade;

    @PostMapping("/shelf/{shelfId}/user/{userId}")
    public ResponseEntity<ShelfDTO> bookShelf(
            @PathVariable Integer shelfId,
            @PathVariable UUID userId) {
        try {
            ShelfDTO result = bookingFacade.bookShelf(shelfId, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/shelf/{shelfId}/cancel")
    public ResponseEntity<ShelfDTO> cancelBooking(@PathVariable Integer shelfId) {
        try {
            ShelfDTO result = bookingFacade.cancelBooking(shelfId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/shelf/{shelfId}/available")
    public ResponseEntity<Boolean> isShelfAvailable(@PathVariable Integer shelfId) {
        boolean available = bookingFacade.isShelfAvailable(shelfId);
        return ResponseEntity.ok(available);
    }
}