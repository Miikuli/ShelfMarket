package org.example.shelf_market.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Приложение работает! Перейдите на /api/shelf-groups или /api/shelves";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint работает!";
    }
}