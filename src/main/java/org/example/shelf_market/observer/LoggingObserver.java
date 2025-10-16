package org.example.shelf_market.observer;

public class LoggingObserver implements ShelfObserver {
    @Override
    public void update(Integer shelfId, String message) {
        System.out.println("[LOG] Полка " + shelfId + ": " + message);
    }
}