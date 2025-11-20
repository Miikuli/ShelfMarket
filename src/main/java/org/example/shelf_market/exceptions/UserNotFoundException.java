package org.example.shelf_market.exceptions;


import java.util.UUID;

public class UserNotFoundException extends BaseException {
  public UserNotFoundException(UUID userId) {
    super("User not found with id: " + userId, "USER_NOT_FOUND");
  }

  public UserNotFoundException(String username) {
    super("User not found with username: " + username, "USER_NOT_FOUND");
  }
}