package org.example.shelf_market.services;

import org.example.shelf_market.dto.UserDTO;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoFactory dtoFactory;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);

        UserDTO expectedDTO = new UserDTO(user.getId(), username, "pass", "Test", "User", "123", "test@test.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(dtoFactory.createUserDTO(user)).thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.getUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByUsername_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername(username);
        });
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Arrange
        UserDTO inputDTO = new UserDTO(null, "newuser", "password", "New", "User", "111", "new@test.com");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("newuser");

        UserDTO expectedDTO = new UserDTO(user.getId(), "newuser", "password", "New", "User", "111", "new@test.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(dtoFactory.createUserDTO(user)).thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.createUser(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        UserDTO inputDTO = new UserDTO(null, "duplicate", "pass", "Dup", "User", "222", "dup@test.com");
        when(userRepository.existsByUsername("duplicate")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(inputDTO);
        });
    }

    @Test
    void searchUsers_WithQuery_ShouldReturnFilteredUsers() {
        // Arrange
        String query = "john";
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("John");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setName("Johnny");

        List<User> users = Arrays.asList(user1, user2);
        UserDTO dto1 = new UserDTO(user1.getId(), "john1", "pass", "John", "Doe", "123", "john1@test.com");
        UserDTO dto2 = new UserDTO(user2.getId(), "johnny", "pass", "Johnny", "Smith", "456", "johnny@test.com");

        when(userRepository.searchUsers(query)).thenReturn(users);
        when(dtoFactory.createUserDTO(user1)).thenReturn(dto1);
        when(dtoFactory.createUserDTO(user2)).thenReturn(dto2);

        // Act
        List<UserDTO> result = userService.searchUsers(query);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).searchUsers(query);
    }
}