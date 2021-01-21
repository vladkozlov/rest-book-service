package com.epam.restbookservice.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookTest {

    @Test
    public void testBorrow() {
        var user = new User();
        var book = new Book();
        user.borrow(book, LocalDate.now().plusDays(1));
        user.getBooks().contains(book);
    }

    @Test
    public void testBorrow_withExpiredElements() {
        var user = new User();
        var book = new Book();
        user.borrow(book, LocalDate.now().minusDays(1));
        assertEquals(Collections.singletonList(book),
                user.getBooksWithOutstandingExpiry(LocalDate.now()));
    }

    @Test
    public void testBorrow_withNotExpiredElements() {
        var user = new User();
        var book = new Book();
        user.borrow(book, LocalDate.now().plusDays(1));
        assertTrue(user.getBooksWithOutstandingExpiry(LocalDate.now()).isEmpty());
    }
}
