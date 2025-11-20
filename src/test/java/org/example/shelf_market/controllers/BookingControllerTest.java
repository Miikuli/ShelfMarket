package org.example.shelf_market.controllers;


import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.facade.BookingFacade;
import org.example.shelf_market.exceptions.ShelfNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingFacade bookingFacade;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void bookShelf_ValidRequest_ShouldReturnOk() {
        // Arrange
        Integer shelfId = 1;
        UUID userId = UUID.randomUUID();
        ShelfDTO expectedShelf = new ShelfDTO();

        when(bookingFacade.bookShelf(shelfId, userId)).thenReturn(expectedShelf);

        // Act
        ResponseEntity<ShelfDTO> response = bookingController.bookShelf(shelfId, userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedShelf, response.getBody());
    }

    @Test
    void bookShelf_ShelfNotFound_ShouldPropagateException() {
        // Arrange
        Integer shelfId = 1;
        UUID userId = UUID.randomUUID();

        when(bookingFacade.bookShelf(shelfId, userId))
                .thenThrow(new ShelfNotFoundException(shelfId));

        // Act & Assert
        assertThrows(ShelfNotFoundException.class, () -> {
            bookingController.bookShelf(shelfId, userId);
        });
    }

    @Test
    void isShelfAvailable_ShouldReturnBoolean() {
        // Arrange
        Integer shelfId = 1;
        when(bookingFacade.isShelfAvailable(shelfId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = bookingController.isShelfAvailable(shelfId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }
}