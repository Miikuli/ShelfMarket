package org.example.shelf_market.client;

import org.example.shelf_market.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "user-service")  // Имя сервиса в Eureka
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    UserResponseDTO getUserById(@PathVariable("id") UUID id);

    @GetMapping("/api/users/{id}/exists")
    Boolean userExists(@PathVariable("id") UUID id);
}