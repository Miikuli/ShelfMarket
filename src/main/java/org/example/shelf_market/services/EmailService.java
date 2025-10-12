package org.example.shelf_market.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}