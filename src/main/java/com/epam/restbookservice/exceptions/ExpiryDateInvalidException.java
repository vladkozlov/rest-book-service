package com.epam.restbookservice.exceptions;

public class ExpiryDateInvalidException extends RuntimeException {
    public ExpiryDateInvalidException(String message) {
        super(message);
    }
}
