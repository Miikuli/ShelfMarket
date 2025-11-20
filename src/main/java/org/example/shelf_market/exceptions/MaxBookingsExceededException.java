package org.example.shelf_market.exceptions;

import java.util.UUID;

public class MaxBookingsExceededException extends BaseException {
  public MaxBookingsExceededException(UUID userId, int maxBookings) {
    super("User " + userId + " exceeded maximum bookings limit: " + maxBookings,
            "MAX_BOOKINGS_EXCEEDED");
  }
}