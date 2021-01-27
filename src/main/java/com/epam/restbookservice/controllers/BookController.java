package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.BookDTO;
import com.epam.restbookservice.exceptions.ExpiryDateInvalidException;
import com.epam.restbookservice.services.BookBorrowService;
import com.epam.restbookservice.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookBorrowService bookBorrowService;

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
    public void borrowBook(@PathVariable Long bookId, @RequestBody String expiryDate) {
        bookBorrowService.borrowABook(bookId, LocalDate.parse(expiryDate));
    }

    @PostMapping("/{bookId}/return")
    public void returnBook(@PathVariable Long bookId) {
        bookBorrowService.returnABook(bookId);
    }

    @PostMapping("/{bookId}/extend")
    public void extendBorrowTime(@PathVariable Long bookId, @RequestBody String expiryDate) {
        bookBorrowService.extendBorrowTime(bookId, LocalDate.parse(expiryDate));
    }

    @GetMapping("/borrowed")
    public List<BookBorrowDTO> getBorrowedBooks() {
        return bookBorrowService.getAllBorrows()
                .stream()
                .map(this::bookBorrowToBookBorrowDTO)
                .collect(Collectors.toList());
    }

    private BookBorrowDTO bookBorrowToBookBorrowDTO(BookBorrow bookBorrow) {
        return BookBorrowDTO.builder()
                .id(bookBorrow.getBook().getId())
                .title(bookBorrow.getBook().getTitle())
                .isbn(bookBorrow.getBook().getISBN())
                .tillDate(bookBorrow.getExpireAt())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiryDateInvalidException.class)
    public String expiryDateIsInvalid(ExpiryDateInvalidException ex) {
        return ex.getMessage();

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public String expiryDateIsCorrupted(DateTimeParseException ex) {
        return ex.getMessage();
    }

}
