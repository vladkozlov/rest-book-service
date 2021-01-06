package com.epam.restbookservice.controller;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    @Autowired
    BookService bookService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return ResponseEntity
                .created(URI.create("/"))
                .body(bookService.saveBook(book));
    }

    @GetMapping()
    public ResponseEntity<Iterable<Book>> getBooks() {
        return ResponseEntity.ok(bookService.getBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBook(id));
    }

}
