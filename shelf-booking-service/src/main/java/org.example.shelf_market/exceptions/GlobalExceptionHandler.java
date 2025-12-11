package org.example.shelf_market.exceptions;

import org.example.shelf_market.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ShelfNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleShelfNotFoundException(ShelfNotFoundException ex,
                                                                      HttpServletRequest request) {
        logger.warn("Shelf not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("SHELF_NOT_FOUND", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex,
                                                                     HttpServletRequest request) {
        logger.warn("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ShelfAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleShelfAlreadyBookedException(ShelfAlreadyBookedException ex,
                                                                           HttpServletRequest request) {
        logger.warn("Shelf already booked: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("SHELF_ALREADY_BOOKED", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MaxBookingsExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxBookingsExceededException(MaxBookingsExceededException ex,
                                                                            HttpServletRequest request) {
        logger.warn("Max bookings exceeded: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("MAX_BOOKINGS_EXCEEDED", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
                                                                          HttpServletRequest request) {
        logger.warn("User already exists: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("USER_ALREADY_EXISTS", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {
        logger.error("Unexpected error: ", ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(GroupHasBookedShelvesException.class)
    public ResponseEntity<ErrorResponse> handleGroupHasBookedShelvesException(
            GroupHasBookedShelvesException ex,
            HttpServletRequest request) {
        logger.warn("Cannot free group: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        // HTTP 423 Locked - ресурс заблокирован
        return ResponseEntity.status(HttpStatus.LOCKED).body(error);
    }
}