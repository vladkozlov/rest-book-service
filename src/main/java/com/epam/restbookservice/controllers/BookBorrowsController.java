package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.dtos.BookBorrowDTO;
import com.epam.restbookservice.dtos.BorrowManagementDTO;
import com.epam.restbookservice.repositories.BookBorrowRepository;
import com.epam.restbookservice.services.BookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookBorrows")
@RequiredArgsConstructor
public class BookBorrowsController implements SecuredController {

    private final BookBorrowService bookBorrowService;
    private final BookBorrowRepository bookBorrowRepository;

    @GetMapping
    public List<BookBorrowDTO> getAllBorrows() {
        return bookBorrowService.getAllBorrows()
                .stream()
                .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BookBorrow addBookBorrow(BorrowManagementDTO borrowDTO) {
        return bookBorrowService.addBorrow(borrowDTO.getBookId(), borrowDTO.getUserId(), LocalDate.parse(borrowDTO.getTillDate()));
    }

    @DeleteMapping("/{bookBorrowId}")
    public void removeBookBorrow(@PathVariable Long bookBorrowId) {
        bookBorrowRepository.deleteById(bookBorrowId);
    }

    @PutMapping("/{bookBorrowId}")
    public BookBorrow editBookBorrow(@PathVariable Long bookBorrowId, BorrowManagementDTO borrowDTO) {
        return bookBorrowService.editBookBorrow(bookBorrowId, borrowDTO.getUserId(), borrowDTO.getBookId(),LocalDate.parse(borrowDTO.getTillDate()));
    }

}
