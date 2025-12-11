package org.example.shelf_market.services;

import org.example.shelf_market.dto.UserDTO;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("userServiceTarget")
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(dtoFactory::createUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return dtoFactory.createUserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return dtoFactory.createUserDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmail());
        }

        User user = convertToEntity(userDTO);
        User saved = userRepository.save(user);
        return dtoFactory.createUserDTO(saved);
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Проверяем username только если он изменился и не null
        if (userDTO.getUsername() != null &&
                !existing.getUsername().equals(userDTO.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDTO.getUsername());
        }

        // Проверяем email только если он изменился и не null
        if (userDTO.getEmail() != null &&
                !existing.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDTO.getEmail());
        }

        // Обновляем только те поля, которые пришли в запросе (не null)
        if (userDTO.getUsername() != null) {
            existing.setUsername(userDTO.getUsername());
        }
        if (userDTO.getPassword() != null) {
            existing.setPassword(userDTO.getPassword());
        }
        if (userDTO.getName() != null) {
            existing.setName(userDTO.getName());
        }
        if (userDTO.getSurname() != null) {
            existing.setSurname(userDTO.getSurname());
        }
        if (userDTO.getPhoneNumber() != null) {
            existing.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getEmail() != null) {
            existing.setEmail(userDTO.getEmail());
        }

        User updated = userRepository.save(existing);
        return dtoFactory.createUserDTO(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(dtoFactory::createUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userExists(UUID id) {
        return userRepository.existsById(id);
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}