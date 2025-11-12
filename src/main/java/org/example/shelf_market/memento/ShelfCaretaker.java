package org.example.shelf_market.memento;

import java.util.Stack;

public class ShelfCaretaker {
    private final Stack<ShelfMemento> history = new Stack<>();

    public void save(ShelfOriginator originator) {
        history.push(originator.saveState());
    }

    public void undo(ShelfOriginator originator) {
        if (!history.isEmpty()) {
            originator.restoreState(history.pop());
        }
    }
}