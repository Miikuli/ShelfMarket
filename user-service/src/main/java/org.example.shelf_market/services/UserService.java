// services/UserService.java - упрощенная версия
package org.example.shelf_market.services;

import org.example.shelf_market.dto.UserDTO;
import org.example.shelf_market.dto.UserResponseDTO;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.exceptions.UserAlreadyExistsException;
import org.example.shelf_market.exceptions.UserNotFoundException;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    // services/UserService.java - обновим метод createUser
    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws UserAlreadyExistsException {
        logger.info("=== Создание пользователя ===");
        logger.info("Получен UserDTO: username={}, email={}",
                userDTO.getUsername(), userDTO.getEmail());

        // Проверяем, существует ли пользователь с таким username
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            logger.warn("Пользователь с username {} уже существует", userDTO.getUsername());
            throw new UserAlreadyExistsException("username", userDTO.getUsername());
        }

        // Проверяем, существует ли пользователь с таким email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warn("Пользователь с email {} уже существует", userDTO.getEmail());
            throw new UserAlreadyExistsException("email", userDTO.getEmail());
        }

        // Проверяем, существует ли пользователь с таким номером телефона
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            logger.warn("Пользователь с номером телефона {} уже существует", userDTO.getPhoneNumber());
            throw new UserAlreadyExistsException("phoneNumber", userDTO.getPhoneNumber());
        }

        // Хэшируем пароль
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        logger.info("Пароль захеширован");

        // Создаем нового пользователя
        String role = (userDTO.getRole() != null && !userDTO.getRole().isEmpty())
                ? userDTO.getRole()
                : "USER";

        logger.info("Создаем User entity с ролью: {}", role);

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(encodedPassword)
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .role(role)
                .build();

        logger.info("User entity создан, сохраняем в БД...");
        User savedUser = userRepository.save(user);
        logger.info("Пользователь сохранен в БД с ID: {}", savedUser.getId());

        // Пробуем создать DTO
        logger.info("Создаем UserDTO из сохраненного User...");
        UserDTO resultDTO = dtoFactory.createUserDTO(savedUser);
        logger.info("UserDTO создан: id={}, username={}, role={}",
                resultDTO.getId(), resultDTO.getUsername(), resultDTO.getRole());

        return resultDTO;
    }

    // Метод для назначения роли админа
    @Transactional
    public UserDTO assignAdminRole(UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole("ADMIN");
        User updatedUser = userRepository.save(user);
        return dtoFactory.createUserDTO(updatedUser);
    }

    // Метод для смены роли
    @Transactional
    public UserDTO changeRole(UUID userId, String newRole) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        return dtoFactory.createUserDTO(updatedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(dtoFactory::createUserDTO)
                .collect(Collectors.toList());
    }

    // Получить пользователей по роли
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRole(role).stream()
                .map(dtoFactory::createUserDTO)
                .collect(Collectors.toList());
    }

    // Проверить, является ли пользователь админом
    public boolean isAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
    }

    // Получить роль пользователя
    public String getUserRole(UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getRole();
    }

    // Получить роль пользователя по username
    public String getUserRole(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return user.getRole();
    }

    // Остальные методы без изменений...
    public UserDTO getUserById(UUID id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return dtoFactory.createUserDTO(user);
    }

    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return dtoFactory.createUserDTO(user);
    }

    public UserResponseDTO getUserResponseByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return dtoFactory.createUserResponseDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            user.setPassword(encodedPassword);
        }

        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getSurname() != null && !userDTO.getSurname().isEmpty()) {
            user.setSurname(userDTO.getSurname());
        }

        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
            user.setRole(userDTO.getRole());
        }

        User updatedUser = userRepository.save(user);
        return dtoFactory.createUserDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public List<UserDTO> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(dtoFactory::createUserDTO)
                .collect(Collectors.toList());
    }

    public UUID getUserIdFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuntimeException("User not authenticated");
            }

            String username = authentication.getName();
            logger.info("Getting user ID for username: {}", username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });

            logger.info("Found user ID: {} for username: {}", user.getId(), username);
            return user.getId();
        } catch (Exception e) {
            logger.error("Error getting user ID from security context: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user ID from security context", e);
        }
    }


    public boolean userExists(UUID id) {
        return userRepository.existsById(id);
    }
}