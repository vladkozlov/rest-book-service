package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.dtos.BookDTO;
import com.epam.restbookservice.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book saveBook(BookDTO request) {
        Book book = new Book();
        book.setISBN(request.getISBN());
        book.setTitle(request.getTitle());

        return bookRepository.save(book);
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> updateBook(Long id, BookDTO request) {
        Optional<Book> optionalBook = getBook(id);
        if (optionalBook.isPresent()) {
            Book book = updateBook(request, optionalBook);
            return Optional.of(bookRepository.save(book));
        }

        return Optional.empty();
    }

    private Book updateBook(BookDTO request, Optional<Book> optionalBook) {
        Book book = optionalBook.get();
        book.setISBN(request.getISBN());
        book.setTitle(request.getTitle());
        return book;
    }

    public void deleteBook(Long id) {
        getBook(id).ifPresent(bookRepository::delete);
    }
}
