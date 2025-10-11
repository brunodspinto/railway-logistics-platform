package org.example.exception;

public class BoxNotFoundException extends ValidationException {
    public BoxNotFoundException(String boxId) {
        super("Box not found: " + boxId);
    }
}
