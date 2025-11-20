package org.example.shelf_market.services;


import org.example.shelf_market.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceProxyTest {

    @Mock
    private UserServiceInterface targetService;

    private UserServiceProxy userServiceProxy;

    private UUID userId;
    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        userServiceProxy = new UserServiceProxy(targetService);
        userId = UUID.randomUUID();
        testUser = new UserDTO(userId, "testuser", "password",
                "John", "Doe", "123456789", "john@test.com");
    }

    @Test
    void getUserById_FirstCall_ShouldCallTargetServiceAndCache() {
        // Arrange
        when(targetService.getUserById(userId)).thenReturn(testUser);

        // Act
        UserDTO result1 = userServiceProxy.getUserById(userId);
        UserDTO result2 = userServiceProxy.getUserById(userId); // Второй вызов

        // Assert
        assertEquals(testUser, result1);
        assertEquals(testUser, result2);
        // Проверяем, что targetService вызвался только один раз благодаря кэшу
        verify(targetService, times(1)).getUserById(userId);
    }

    @Test
    void createUser_ShouldInvalidateCache() {
        // Arrange
        when(targetService.createUser(testUser)).thenReturn(testUser);
        when(targetService.getAllUsers()).thenReturn(java.util.List.of(testUser));

        // Act - сначала кэшируем данные
        userServiceProxy.getAllUsers();
        // Затем создаем пользователя (должен очистить кэш)
        userServiceProxy.createUser(testUser);
        // Снова запрашиваем всех пользователей
        userServiceProxy.getAllUsers();

        // Assert - после создания пользователя кэш очищается и targetService вызывается снова
        verify(targetService, times(2)).getAllUsers();
    }

    @Test
    void clearAllCache_ShouldClearAllCachedData() {
        // Arrange
        when(targetService.getUserById(userId)).thenReturn(testUser);

        // Act - кэшируем данные
        userServiceProxy.getUserById(userId);
        // Очищаем кэш
        userServiceProxy.clearAllCache();
        // Снова запрашиваем те же данные
        userServiceProxy.getUserById(userId);

        // Assert - после очистки кэша targetService вызывается снова
        verify(targetService, times(2)).getUserById(userId);
    }
}