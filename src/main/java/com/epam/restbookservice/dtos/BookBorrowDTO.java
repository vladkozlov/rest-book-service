package com.epam.restbookservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookBorrowDTO {

    private Long id;
    private String title;
    private String isbn;
    private LocalDate tillDate;

}
