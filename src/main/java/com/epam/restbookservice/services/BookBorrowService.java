package com.epam.restbookservice.services;

import com.epam.restbookservice.repositories.BookBorrowRepository;
import com.epam.restbookservice.repositories.BookRepository;
import com.epam.restbookservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookBorrowService {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookBorrowService(BookService bookService, BookRepository bookRepository, UserRepository userRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public void borrowABook(Long bookId, LocalDate tillDate) {
    }
}
