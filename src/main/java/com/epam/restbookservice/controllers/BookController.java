package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.dtos.BookDTO;
import com.epam.restbookservice.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookDTO request) {
        return ResponseEntity
                .created(URI.create("/"))
                .body(bookService.saveBook(request));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        return ResponseEntity.ok(bookService.getBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Book not found with id %s", id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookDTO request) {
        return ResponseEntity.ok(bookService.updateBook(id, request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Book not found with id %s", id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/borrow")
    public void borrowBook(@PathVariable Long bookId) {
        bookService.borrowBook(bookId);
    }

}
