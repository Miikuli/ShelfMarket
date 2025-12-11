package org.example.shelf_market.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {
        // Заглушка для демонстрации
        logger.info("📧 Email sent to: {}", to);
        logger.info("📧 Subject: {}", subject);
        logger.info("📧 Body: {}", body);

        // В реальном приложении здесь будет интеграция с почтовым сервером
        // например, через JavaMailSender, SendGrid, etc.
    }
}