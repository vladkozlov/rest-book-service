package com.epam.restbookservice.dtos;

import lombok.Data;

@Data
public class BorrowManagementDTO {

    private Long userId;
    private Long bookId;
    private String tillDate;

}
