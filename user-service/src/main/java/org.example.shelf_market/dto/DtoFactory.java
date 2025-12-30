package org.example.shelf_market.dto;

import org.example.shelf_market.entities.User;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// dto/DtoFactory.java
@Component
public class DtoFactory {

    private static final Logger logger = LoggerFactory.getLogger(DtoFactory.class);

    public UserDTO createUserDTO(User user) {
        logger.info("Создание UserDTO из User: id={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());

        if (user == null) {
            logger.error("User is null!");
            return null;
        }

        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(null); // Не возвращаем пароль
            userDTO.setName(user.getName());
            userDTO.setSurname(user.getSurname());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole());

            logger.info("UserDTO успешно создан: {}", userDTO);
            return userDTO;
        } catch (Exception e) {
            logger.error("Ошибка создания UserDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UserResponseDTO createUserResponseDTO(User user) {
        logger.info("Создание UserResponseDTO из User: id={}, username={}",
                user.getId(), user.getUsername());

        if (user == null) {
            logger.error("User is null!");
            return null;
        }

        try {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setId(user.getId());
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setPassword(user.getPassword()); // Возвращаем пароль для внутренних нужд
            userResponseDTO.setName(user.getName());
            userResponseDTO.setSurname(user.getSurname());
            userResponseDTO.setPhoneNumber(user.getPhoneNumber());
            userResponseDTO.setEmail(user.getEmail());

            logger.info("UserResponseDTO успешно создан: {}", userResponseDTO);
            return userResponseDTO;
        } catch (Exception e) {
            logger.error("Ошибка создания UserResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }
}