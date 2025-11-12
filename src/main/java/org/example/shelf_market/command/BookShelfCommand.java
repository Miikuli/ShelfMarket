package org.example.shelf_market.command;

import org.example.shelf_market.services.ShelfService;

public class BookShelfCommand implements Command {
    private final ShelfService shelfService;
    private final Integer shelfId;
    private final Integer userId;

    public BookShelfCommand(ShelfService shelfService, Integer shelfId, Integer userId) {
        this.shelfService = shelfService;
        this.shelfId = shelfId;
        this.userId = userId;
    }

    @Override
    public void execute() {
        shelfService.bookShelf(shelfId, userId);
    }

    @Override
    public void undo() {
        shelfService.cancelBooking(shelfId);
    }
}