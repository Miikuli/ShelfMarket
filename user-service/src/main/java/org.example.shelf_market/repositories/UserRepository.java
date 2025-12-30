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

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(@Param("query") String query);

    // Новые методы для работы с ролями
    List<User> findByRole(String role);
    long countByRole(String role);
    Optional<User> findByUsernameAndRole(String username, String role);

}