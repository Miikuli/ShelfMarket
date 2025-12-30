package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.dto.UserResponseDTO;
import org.example.shelf_market.facade.BookingFacade;
import org.example.shelf_market.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingFacade bookingFacade;

    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/shelf/{shelfId}/user/{userId}")
    public ResponseEntity<ShelfDTO> bookShelf(
            @PathVariable Integer shelfId,
            @PathVariable UUID userId) {

        logger.info("Booking request for shelf {} by user {}", shelfId, userId);
        ShelfDTO result = bookingFacade.bookShelf(shelfId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/shelf/{shelfId}/cancel")
    public ResponseEntity<ShelfDTO> cancelBooking(@PathVariable Integer shelfId, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        UUID userId = UUID.fromString((String) request.getAttribute("userId"));
        String role = (String) request.getAttribute("role");
        boolean isAdmin = "ADMIN".equals(role);
        logger.info("Cancel booking request for shelf {} by user {}", shelfId, userId);
        ShelfDTO result = bookingFacade.cancelBooking(shelfId, userId, isAdmin);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/shelf/{shelfId}/available")
    public ResponseEntity<Boolean> isShelfAvailable(@PathVariable Integer shelfId) {
        boolean available = bookingFacade.isShelfAvailable(shelfId);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/shelf/{shelfId}")
    public ResponseEntity<ShelfDTO> getShelfInfo(@PathVariable Integer shelfId) {
        ShelfDTO shelfInfo = bookingFacade.getShelfInfo(shelfId);
        return ResponseEntity.ok(shelfInfo);
    }

    @GetMapping("/user/{userId}/active-count")
    public ResponseEntity<Long> getUserActiveBookingsCount(@PathVariable UUID userId) {
        long count = bookingFacade.getUserActiveBookingsCount(userId);
        return ResponseEntity.ok(count);
    }
}