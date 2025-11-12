package org.example.shelf_market.observer;

import java.util.ArrayList;
import java.util.List;

public class ShelfSubject {
    private final List<ShelfObserver> observers = new ArrayList<>();

    public void attach(ShelfObserver observer) {
        observers.add(observer);
    }

    public void detach(ShelfObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Integer shelfId, String message) {
        for (ShelfObserver observer : observers) {
            observer.update(shelfId, message);
        }
    }
}