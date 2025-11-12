package org.example.shelf_market.command;

public interface Command {
    void execute();
    void undo();
}