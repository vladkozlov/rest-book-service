package com.epam.restbookservice.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookBorrow {

    private final Book book;
    private final LocalDate expireAt;

    public BookBorrow(Book book, LocalDate expireDate) {
        this.book = book;
        this.expireAt = expireDate;
    }
}
