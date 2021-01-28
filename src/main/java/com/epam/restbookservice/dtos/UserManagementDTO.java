package com.epam.restbookservice.dtos;

import com.epam.restbookservice.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserManagementDTO {

    private UserDTO user;

    private List<BookBorrowDTO> bookBorrow;


    public static UserManagementDTO userToUserManagementDTO(User user) {
        return UserManagementDTO
                .builder()
                .user(UserDTO.userToUserDTO(user))
                .bookBorrow(user
                        .getBorrowedBooks()
                        .stream()
                        .map(BookBorrowDTO::bookBorrowToBookBorrowDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
