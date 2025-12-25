package org.example.shelf_market.controllers;;

import org.example.shelf_market.dto.UserDTO;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TestController {

    @GetMapping("/api/users/test")  // Убедитесь, что путь правильный
    public String test() {
        return "Test endpoint works!";
    }

    @GetMapping("/api/auth/test")
    public String authTest() {
        return "Auth test works!";
    }
}