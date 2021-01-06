package com.epam.restbookservice.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Book {

    @Id
    private String ISBN;

    private String name;
}
