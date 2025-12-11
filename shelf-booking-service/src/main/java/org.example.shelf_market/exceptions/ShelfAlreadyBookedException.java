package org.example.shelf_market.exceptions;

public class ShelfAlreadyBookedException extends BaseException {
  public ShelfAlreadyBookedException(Integer shelfId) {
    super("Shelf already booked: " + shelfId, "SHELF_ALREADY_BOOKED");
  }
}