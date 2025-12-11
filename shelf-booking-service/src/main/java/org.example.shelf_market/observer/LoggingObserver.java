package org.example.shelf_market.observer;

import org.springframework.stereotype.Component;

@Component
public class LoggingObserver implements ShelfObserver {
    @Override
    public void update(Integer shelfId, String message) {
        System.out.println("[LOG] Полка " + shelfId + ": " + message);
    }
}