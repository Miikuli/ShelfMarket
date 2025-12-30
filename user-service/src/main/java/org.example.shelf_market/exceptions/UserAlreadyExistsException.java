package org.example.shelf_market.exceptions;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value, "USER_ALREADY_EXISTS");
    }
}