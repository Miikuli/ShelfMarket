package org.example.shelf_market.exceptions;

public class UnauthorizedCancellationException extends BaseException {

    public UnauthorizedCancellationException(String message) {
        super(message, "UNAUTHORIZED_CANCELLATION");
    }

}