package org.example.shelf_market.observer;

import org.example.shelf_market.services.EmailService;
import org.springframework.stereotype.Component;

@Component
public class EmailObserver implements ShelfObserver {
    private final EmailService emailService;

    public EmailObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    // Например, отправляем сообщение автору о том, что полочка освободилась для брони
    @Override
    public void update(Integer shelfId, String message) {
        // можно получить email пользователя по shelfId, здесь упрощенно
        emailService.sendEmail("user@example.com",
                "Изменение статуса полочки",
                "Полка " + shelfId + ": " + message);
    }
}
