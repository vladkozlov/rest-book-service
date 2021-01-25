package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.dtos.BookDTO;
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

import java.net.URI;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

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
}
