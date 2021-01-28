package com.epam.restbookservice.dtos;

import com.epam.restbookservice.domain.Role;
import com.epam.restbookservice.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
public class UserDTO {
    @NotNull
    private String username;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isEnabled;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> roles;


    public static UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isEnabled(user.isEnabled())
                .roles(getListOfStringRoles(user))
                .build();
    }


    private static List<String> getListOfStringRoles(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .map(getOriginalRoleName())
                .collect(Collectors.toList());
    }

    private static Function<String, String> getOriginalRoleName() {
        return role -> role.substring(5);
    }

}
