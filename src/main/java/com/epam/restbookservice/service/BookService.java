package com.epam.restbookservice.service;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Iterable<Book> getBooks() {
        return bookRepository.findAll();
    }
}
