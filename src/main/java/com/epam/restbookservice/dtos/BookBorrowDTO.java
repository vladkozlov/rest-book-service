package com.epam.restbookservice.dtos;

import com.epam.restbookservice.domain.BookBorrow;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookBorrowDTO {

    private Long id;
    private Long userId;
    private Long bookId;
    private String title;
    private String isbn;
    private LocalDate tillDate;

    public static BookBorrowDTO bookBorrowToBookBorrowDTO(BookBorrow bookBorrow) {
        return BookBorrowDTO.builder()
                .id(bookBorrow.getId())
                .bookId(bookBorrow.getBook().getId())
                .userId(bookBorrow.getUser().getId())
                .title(bookBorrow.getBook().getTitle())
                .isbn(bookBorrow.getBook().getISBN())
                .tillDate(bookBorrow.getExpireAt())
                .build();
    }

}
