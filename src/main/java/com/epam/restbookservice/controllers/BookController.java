package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.BookDTO;
import com.epam.restbookservice.exceptions.ExpiryDateInvalidException;
import com.epam.restbookservice.services.BookBorrowService;
import com.epam.restbookservice.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Books", description = "Managing books")
public class BookController implements SecuredController {

    private final BookService bookService;
    private final BookBorrowService bookBorrowService;

    @Operation(summary = "Create a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created the book",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input supplied",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookDTO request) {
        return ResponseEntity
                .created(URI.create("/"))
                .body(bookService.saveBook(request));
    }


    @Operation(summary = "Get all book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the books",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @GetMapping
    public ResponseEntity<List<Book>> getBooks() {
        return ResponseEntity.ok(bookService.getBooks());
    }

    @Operation(summary = "Search for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchBooks(title));
    }

    @Operation(summary = "Get a book by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBook(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Book not found with id %s", id))));
    }

    @Operation(summary = "Update a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the book",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookDTO request) {
        return ResponseEntity.ok(bookService.updateBook(id, request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("Book not found with id %s", id))));
    }

    @Operation(summary = "Delete a book by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the book"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Borrow a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowed the book"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PostMapping("/{bookId}/borrow")
    public void borrowBook(@PathVariable Long bookId, @RequestBody String expiryDate) {
        bookBorrowService.borrowABook(bookId, LocalDate.parse(expiryDate));
    }

    @Operation(summary = "Return a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned the book"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PostMapping("/{bookId}/return")
    public void returnBook(@PathVariable Long bookId) {
        bookBorrowService.returnABook(bookId);
    }

    @Operation(summary = "Extend the borrow time for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Extended the book borrow time"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PostMapping("/{bookId}/extend")
    public void extendBorrowTime(@PathVariable Long bookId, @RequestBody String expiryDate) {
        bookBorrowService.extendBorrowTime(bookId, LocalDate.parse(expiryDate));
    }

    @Operation(summary = "Get borrowed books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the borrowed books"),
            @ApiResponse(responseCode = "404", description = "Borrowed book not found",
                    content = @Content) })
    @GetMapping("/borrowed")
    public List<BookBorrowDTO> getBorrowedBooks() {
        return bookBorrowService.getAllBorrows()
                .stream()
                .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                .collect(Collectors.toList());
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
