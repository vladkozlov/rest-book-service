package com.epam.restbookservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserManagementDTO {

    private UserDTO user;

    private List<BookBorrowDTO> bookBorrow;
}
