package com.epam.restbookservice.controllers;

import static java.lang.String.format;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
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

    @Operation(summary = "Get a book by its id")
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
