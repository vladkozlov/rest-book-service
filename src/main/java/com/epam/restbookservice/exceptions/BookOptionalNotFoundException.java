package com.epam.restbookservice.exceptions;

public class BookOptionalNotFoundException extends RuntimeException {
    public BookOptionalNotFoundException(String message) {
        super(message);
    }
}
