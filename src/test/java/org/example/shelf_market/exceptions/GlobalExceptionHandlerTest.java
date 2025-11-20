package org.example.shelf_market.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void handleShelfNotFoundException_ShouldReturn404() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/shelves/999");
        ShelfNotFoundException exception = new ShelfNotFoundException(999);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleShelfNotFoundException(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleUserNotFoundException_ShouldReturn404() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users/123");
        UserNotFoundException exception = new UserNotFoundException(UUID.randomUUID());

        // Act
        ResponseEntity<?> response = exceptionHandler.handleUserNotFoundException(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleShelfAlreadyBookedException_ShouldReturn409() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/bookings/shelf/1");
        ShelfAlreadyBookedException exception = new ShelfAlreadyBookedException(1);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleShelfAlreadyBookedException(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleMaxBookingsExceededException_ShouldReturn409() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/bookings");
        MaxBookingsExceededException exception = new MaxBookingsExceededException(userId, 5);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleMaxBookingsExceededException(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturn409() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/users");
        UserAlreadyExistsException exception = new UserAlreadyExistsException("username", "testuser");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleUserAlreadyExistsException(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/test");
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleGenericException(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}