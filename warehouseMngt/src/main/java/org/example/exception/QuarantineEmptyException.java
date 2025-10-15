package org.example.exception;

public class QuarantineEmptyException extends RuntimeException {

    public QuarantineEmptyException(String message) {
        super(message);
    }

    public QuarantineEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}

