package com.epam.restbookservice.exceptions;

public class BookIsBorrowedException extends RuntimeException {
    public BookIsBorrowedException(String message) {
        super(message);
    }
}
