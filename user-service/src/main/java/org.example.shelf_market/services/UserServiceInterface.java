package org.example.shelf_market.services;

import org.example.shelf_market.dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface UserServiceInterface {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(UUID id);
    UserDTO getUserByUsername(String username);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UUID id, UserDTO userDTO);
    void deleteUser(UUID id);
    List<UserDTO> searchUsers(String query);
    boolean userExists(UUID id);
}