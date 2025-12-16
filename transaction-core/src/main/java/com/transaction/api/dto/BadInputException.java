package com.transaction.api.dto;

public class BadInputException extends RuntimeException {
    public BadInputException(String message) {
        super(message);
    }
    public BadInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
