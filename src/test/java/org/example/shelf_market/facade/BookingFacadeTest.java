package org.example.shelf_market.facade;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.exceptions.*;
import org.example.shelf_market.observer.ShelfSubject;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingFacadeTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private ShelfSubject shelfSubject;

    @InjectMocks
    private BookingFacade bookingFacade;

    private Integer shelfId;
    private UUID userId;
    private Shelf shelf;
    private User user;

    @BeforeEach
    void setUp() {
        shelfId = 1;
        userId = UUID.randomUUID();

        shelf = new Shelf();
        shelf.setId(shelfId);
        shelf.setBooked(false);

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
    }

    @Test
    void bookShelf_WhenShelfAvailableAndUserExists_ShouldBookShelf() {
        // Arrange
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shelfRepository.countByUserIdAndBookedTrue(userId)).thenReturn(0L);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        ShelfDTO expectedDto = new ShelfDTO();
        when(dtoFactory.createShelfDTO(shelf)).thenReturn(expectedDto);

        // Act
        ShelfDTO result = bookingFacade.bookShelf(shelfId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(shelfRepository).save(shelf);
        verify(shelfSubject).notifyObservers(eq(shelfId), anyString());
    }

    @Test
    void bookShelf_WhenShelfNotFound_ShouldThrowException() {
        // Arrange
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShelfNotFoundException.class, () -> {
            bookingFacade.bookShelf(shelfId, userId);
        });
    }

    @Test
    void bookShelf_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            bookingFacade.bookShelf(shelfId, userId);
        });
    }

    @Test
    void bookShelf_WhenShelfAlreadyBooked_ShouldThrowException() {
        // Arrange
        shelf.setBooked(true); // Полка уже забронирована!

        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user)); // ← ИСПРАВЛЕНИЕ!

        // Act & Assert
        assertThrows(ShelfAlreadyBookedException.class, () -> {
            bookingFacade.bookShelf(shelfId, userId);
        });
    }

    @Test
    void cancelBooking_WhenShelfBooked_ShouldCancelBooking() {
        // Arrange
        shelf.setBooked(true);
        shelf.setUser(user);

        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(any(Shelf.class))).thenReturn(shelf);

        ShelfDTO expectedDto = new ShelfDTO();
        when(dtoFactory.createShelfDTO(shelf)).thenReturn(expectedDto);

        // Act
        ShelfDTO result = bookingFacade.cancelBooking(shelfId);

        // Assert
        assertNotNull(result);
        assertFalse(shelf.getBooked());
        assertNull(shelf.getUser());
        verify(shelfRepository).save(shelf);
    }

    @Test
    void isShelfAvailable_WhenShelfExistsAndNotBooked_ShouldReturnTrue() {
        // Arrange
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));

        // Act
        boolean available = bookingFacade.isShelfAvailable(shelfId);

        // Assert
        assertTrue(available);
    }
}