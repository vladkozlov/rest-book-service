package com.epam.restbookservice.controllers;

import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.BorrowManagementDTO;
import com.epam.restbookservice.repositories.BookBorrowRepository;
import com.epam.restbookservice.services.BookBorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookBorrows")
@RequiredArgsConstructor
@Tag(name = "Book borrows", description = "Managing book borrows")
public class BookBorrowsController implements SecuredController {

    private final BookBorrowService bookBorrowService;
    private final BookBorrowRepository bookBorrowRepository;

    @Operation(summary = "Get book borrows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the borrowed books"),
            @ApiResponse(responseCode = "404", description = "Borrowed book not found",
                    content = @Content) })
    @GetMapping
    public List<BookBorrowDTO> getAllBorrows() {
        return bookBorrowService.getAllBorrows()
                .stream()
                .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Borrow a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowed the book")})
    @PostMapping
    public BookBorrowDTO addBookBorrow(@RequestBody BorrowManagementDTO borrowDTO) {
        return BookBorrowDTO.bookBorrowToBookBorrowDTO(
                bookBorrowService.addBorrow(
                        borrowDTO.getBookId(),
                        borrowDTO.getUserId(),
                        LocalDate.parse(borrowDTO.getTillDate()))
        );
    }

    @Operation(summary = "Delete a book borrow by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the book borrow"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book borrow not found",
                    content = @Content) })
    @DeleteMapping("/{bookBorrowId}")
    public void removeBookBorrow(@PathVariable Long bookBorrowId) {
        bookBorrowRepository.deleteById(bookBorrowId);
    }

    @Operation(summary = "Update a book borrow")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the book borrow",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookBorrowDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content) })
    @PutMapping("/{bookBorrowId}")
    public BookBorrowDTO editBookBorrow(@PathVariable Long bookBorrowId, @RequestBody BorrowManagementDTO borrowDTO) {
        return BookBorrowDTO.bookBorrowToBookBorrowDTO(
                bookBorrowService.editBookBorrow(
                        bookBorrowId,
                        borrowDTO.getUserId(),
                        borrowDTO.getBookId(),
                        LocalDate.parse(borrowDTO.getTillDate()))
        );
    }

}
