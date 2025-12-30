package org.example.shelf_market.memento;

import lombok.Getter;

@Getter
public class ShelfMemento {
    private final String state;

    public ShelfMemento(String state) {
        this.state = state;
    }

}