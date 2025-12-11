package org.example.shelf_market.memento;

public class ShelfOriginator {
    private String currentState;

    public void setState(String state) {
        currentState = state;
    }

    public String getState() {
        return currentState;
    }

    public ShelfMemento saveState() {
        return new ShelfMemento(currentState);
    }

    public void restoreState(ShelfMemento memento) {
        this.currentState = memento.getState();
    }
}