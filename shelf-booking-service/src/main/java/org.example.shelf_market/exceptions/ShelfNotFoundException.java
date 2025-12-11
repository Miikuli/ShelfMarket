package org.example.shelf_market.exceptions;

public class ShelfNotFoundException extends BaseException {
    public ShelfNotFoundException(Integer shelfId) {
        super("Shelf not found with id: " + shelfId, "SHELF_NOT_FOUND");
    }
}