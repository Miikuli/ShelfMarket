package org.example.shelf_market.controllers;

import org.example.shelf_market.dto.UserDTO;
import org.example.shelf_market.services.UserService;
import org.example.shelf_market.exceptions.UserNotFoundException;
import org.example.shelf_market.exceptions.UserAlreadyExistsException;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnList() {
        // Arrange
        UserDTO user1 = new UserDTO(UUID.randomUUID(), "user1", "pass1", "John", "Doe", "123", "john@test.com");
        UserDTO user2 = new UserDTO(UUID.randomUUID(), "user2", "pass2", "Jane", "Smith", "456", "jane@test.com");
        List<UserDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO user = new UserDTO(userId, "testuser", "pass", "Test", "User", "789", "test@test.com");
        when(userService.getUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturn404() {
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId))
                .thenThrow(new UserNotFoundException(userId));

        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        UserDTO inputUser = new UserDTO(null, "newuser", "password", "New", "User", "111", "new@test.com");
        UserDTO createdUser = new UserDTO(UUID.randomUUID(), "newuser", "password", "New", "User", "111", "new@test.com");

        when(userService.createUser(inputUser)).thenReturn(createdUser);

        ResponseEntity<UserDTO> response = userController.createUser(inputUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getUsername());
    }

    @Test
    void createUser_WithDuplicateUsername_ShouldReturn400() {
        UserDTO inputUser = new UserDTO(null, "duplicate", "pass", "Dup", "User", "222", "dup@test.com");
        when(userService.createUser(inputUser))
                .thenThrow(new UserAlreadyExistsException("username", "duplicate"));

        ResponseEntity<UserDTO> response = userController.createUser(inputUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // ИЛИ HttpStatus.CONFLICT
    }

    @Test
    void searchUsers_WithQuery_ShouldReturnFilteredUsers() {
        // Arrange
        String query = "john";
        UserDTO user = new UserDTO(UUID.randomUUID(), "john", "pass", "John", "Doe", "333", "john@test.com");
        List<UserDTO> users = Arrays.asList(user);

        when(userService.searchUsers(query)).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.searchUsers(query);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO inputUser = new UserDTO(userId, "updated", "pass", "Updated", "User", "444", "updated@test.com");
        UserDTO updatedUser = new UserDTO(userId, "updated", "pass", "Updated", "User", "444", "updated@test.com");

        when(userService.updateUser(userId, inputUser)).thenReturn(updatedUser);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(userId, inputUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updated", response.getBody().getUsername());
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldReturn404() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO inputUser = new UserDTO(userId, "nonexistent", "pass", "None", "User", "555", "none@test.com");
        when(userService.updateUser(userId, inputUser))
                .thenThrow(new UserNotFoundException(userId));

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(userId, inputUser);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnOk() {
        // Arrange
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).deleteUser(userId);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturn404() {
        // Arrange
        UUID userId = UUID.randomUUID();
        doThrow(new UserNotFoundException(userId))
                .when(userService).deleteUser(userId);

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}