package org.example.shelf_market.exceptions;


public class ShelfNotBookedException extends BaseException {
  public ShelfNotBookedException(Integer shelfId) {
    super("Shelf is not booked: " + shelfId, "SHELF_NOT_BOOKED");
  }
}