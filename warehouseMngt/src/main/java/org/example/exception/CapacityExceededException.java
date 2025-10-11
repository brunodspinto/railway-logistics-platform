package org.example.exception;

public class CapacityExceededException extends ValidationException {
    public CapacityExceededException(String location, int capacity) {
        super("Bay " + location + " is full (capacity: " + capacity + ")");
    }
}
