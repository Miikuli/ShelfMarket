package org.example.shelf_market.repositories;

import org.example.shelf_market.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> { // ← ИЗМЕНИТЬ НА UUID!

    // Найти пользователя по username
    Optional<User> findByUsername(String username);

    // Найти пользователя по email
    Optional<User> findByEmail(String email);

    // Поиск по имени и фамилии
    List<User> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);

    // Проверить существование пользователя по username
    boolean existsByUsername(String username);

    // Проверить существование пользователя по email
    boolean existsByEmail(String email);

    // Кастомный запрос для поиска по части имени
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(@Param("query") String query);

}