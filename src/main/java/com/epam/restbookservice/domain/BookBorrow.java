package com.epam.restbookservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "book_borrow")
public class BookBorrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    private LocalDate expireAt;

    public BookBorrow(Book book, LocalDate expiryDate) {
        this.book = book;
        this.expireAt = expiryDate;
    }

    public BookBorrow(User user, Book book, LocalDate expiryDate) {
        this.user = user;
        this.book = book;
        this.expireAt = expiryDate;
    }
}
