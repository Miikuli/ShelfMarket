package org.example.shelf_market.command;

import org.example.shelf_market.services.ShelfService;

public class CancelBookingCommand implements Command {
    private final ShelfService shelfService;
    private final Integer shelfId;

    public CancelBookingCommand(ShelfService shelfService, Integer shelfId) {
        this.shelfService = shelfService;
        this.shelfId = shelfId;
    }

    @Override
    public void execute() {
        shelfService.cancelBooking(shelfId);
    }

    @Override
    public void undo() {
        // можно реализовать повторное бронирование если нужно
    }
}