package com.epam.restbookservice.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BookDTO {

    @NotNull
    String ISBN;

    @NotNull
    String title;
}
