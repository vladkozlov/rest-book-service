package com.epam.restbookservice.dtos;

import com.epam.restbookservice.domain.Role;
import com.sun.istack.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private List<Role> roles;
}
