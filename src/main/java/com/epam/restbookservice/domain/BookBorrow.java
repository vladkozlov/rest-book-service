package com.epam.restbookservice.domain;

import java.time.LocalDate;

public class BookBorrow {

    private Book book;
    private LocalDate expiry;

    public BookBorrow(Book book, LocalDate expiryDate) {
        this.book = book;
        this.expiry = expiryDate;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getExpiry() {
        return expiry;
    }
}
