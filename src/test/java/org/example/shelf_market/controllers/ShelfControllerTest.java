package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.services.ShelfService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfControllerTest {

    @Mock
    private ShelfService shelfService;

    @InjectMocks
    private ShelfController shelfController;

    @Test
    void getAllShelves_WhenShelvesExist_ShouldReturnList() {
        // Arrange
        ShelfDTO shelf1 = new ShelfDTO(1, false, 1, null);
        ShelfDTO shelf2 = new ShelfDTO(2, true, 1, UUID.randomUUID());
        List<ShelfDTO> shelves = Arrays.asList(shelf1, shelf2);

        when(shelfService.getAllShelves()).thenReturn(shelves);

        // Act
        List<ShelfDTO> result = shelfController.getAllShelves();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(shelfService).getAllShelves();
    }

    @Test
    void getShelfById_WhenShelfExists_ShouldReturnShelf() {
        // Arrange
        Integer shelfId = 1;
        ShelfDTO shelf = new ShelfDTO(shelfId, false, 1, null);
        when(shelfService.getShelfById(shelfId)).thenReturn(shelf);

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.getShelfById(shelfId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shelfId, response.getBody().getId());
    }

    @Test
    void getShelfById_WhenShelfNotExists_ShouldReturn404() {
        // Arrange
        Integer shelfId = 999;
        when(shelfService.getShelfById(shelfId))
                .thenThrow(new RuntimeException("Shelf not found"));

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.getShelfById(shelfId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createShelf_WithValidData_ShouldCreateShelf() {
        // Arrange
        ShelfDTO inputShelf = new ShelfDTO(null, false, 1, null);
        ShelfDTO createdShelf = new ShelfDTO(1, false, 1, null);

        when(shelfService.createShelf(inputShelf)).thenReturn(createdShelf);

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.createShelf(inputShelf); // ← Теперь ResponseEntity!

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(shelfService).createShelf(inputShelf);
    }

    @Test
    void createShelf_WithMissingShelfGroup_ShouldReturn400() {
        // Arrange
        ShelfDTO invalidShelf = new ShelfDTO(null, false, null, null); // Нет shelfGroupNumber

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.createShelf(invalidShelf);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(shelfService, never()).createShelf(any());
    }

    @Test
    void createShelf_WithBookedButNoUser_ShouldReturn400() {
        // Arrange
        ShelfDTO invalidShelf = new ShelfDTO(null, true, 1, null); // Забронирована, но нет пользователя

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.createShelf(invalidShelf);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(shelfService, never()).createShelf(any());
    }

    @Test
    void createShelf_WhenServiceThrowsException_ShouldReturn400() {
        // Arrange
        ShelfDTO inputShelf = new ShelfDTO(null, false, 1, null);
        when(shelfService.createShelf(inputShelf))
                .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.createShelf(inputShelf);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getShelvesByGroup_WhenGroupExists_ShouldReturnShelves() {
        // Arrange
        Integer groupNumber = 1;
        ShelfDTO shelf1 = new ShelfDTO(1, false, groupNumber, null);
        ShelfDTO shelf2 = new ShelfDTO(2, true, groupNumber, UUID.randomUUID());
        List<ShelfDTO> shelves = Arrays.asList(shelf1, shelf2);

        when(shelfService.getShelvesByGroup(groupNumber)).thenReturn(shelves);

        // Act
        List<ShelfDTO> result = shelfController.getShelvesByGroup(groupNumber);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getShelfGroupNumber().equals(groupNumber)));
    }

    @Test
    void deleteShelf_WhenShelfExists_ShouldReturnOk() {
        // Arrange
        Integer shelfId = 1;
        doNothing().when(shelfService).deleteShelf(shelfId);

        // Act
        ResponseEntity<Void> response = shelfController.deleteShelf(shelfId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(shelfService).deleteShelf(shelfId);
    }

    @Test
    void deleteShelf_WhenShelfNotExists_ShouldReturn404() {
        // Arrange
        Integer shelfId = 999;
        doThrow(new RuntimeException("Shelf not found"))
                .when(shelfService).deleteShelf(shelfId);

        // Act
        ResponseEntity<Void> response = shelfController.deleteShelf(shelfId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateShelf_WhenShelfExists_ShouldUpdateShelf() {
        // Arrange
        Integer shelfId = 1;
        ShelfDTO inputShelf = new ShelfDTO(shelfId, true, 1, UUID.randomUUID());
        ShelfDTO updatedShelf = new ShelfDTO(shelfId, true, 1, UUID.randomUUID());

        when(shelfService.updateShelf(shelfId, inputShelf)).thenReturn(updatedShelf);

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.updateShelf(shelfId, inputShelf);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(shelfId, response.getBody().getId());
    }

    @Test
    void updateShelf_WhenShelfNotExists_ShouldReturn404() {
        // Arrange
        Integer shelfId = 999;
        ShelfDTO inputShelf = new ShelfDTO(shelfId, true, 1, UUID.randomUUID());
        when(shelfService.updateShelf(shelfId, inputShelf))
                .thenThrow(new RuntimeException("Shelf not found"));

        // Act
        ResponseEntity<ShelfDTO> response = shelfController.updateShelf(shelfId, inputShelf);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}