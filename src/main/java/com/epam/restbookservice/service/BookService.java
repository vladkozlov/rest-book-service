package com.epam.restbookservice.service;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.exception.BookNotFoundException;
import com.epam.restbookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

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

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(format("Book not found with id %s", id)));
    }

    public Book updateBook(Long id, Book request) {
        Book book = getBook(id);
        book.setISBN(request.getISBN());
        book.setName(request.getName());

        return saveBook(book);
    }
}
