package org.example.shelf_market.exceptions;

public class GroupHasBookedShelvesException extends BaseException {
    public GroupHasBookedShelvesException(Integer groupNumber) {
        super("Cannot set group " + groupNumber + " to available because it has booked shelves. " +
                        "First free all shelves in the group.",
                "GROUP_HAS_BOOKED_SHELVES");
    }
}