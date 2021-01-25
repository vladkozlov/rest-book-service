package com.epam.restbookservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "security_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<BookBorrow> borrowedBooks;

    public User(String username, String password, String firstName, String lastName, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = Arrays.asList(role);
        this.isEnabled = true;
        this.borrowedBooks = Collections.emptyList();
    }

    public User(String username, String password, String firstName, String lastName, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.isEnabled = true;
        this.borrowedBooks = Collections.emptyList();
    }

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.borrowedBooks = Collections.emptyList();
    }

    public void borrow(BookBorrow bookBorrow) {
        borrowedBooks.add(bookBorrow);
    }

    public void borrowABook(Book book, LocalDate expireDate) {
        borrowedBooks.add(new BookBorrow(book, expireDate));
    }

    public void returnABook(Book book) {
        borrowedBooks.removeIf(bookBorrow ->
                bookBorrow.getBook().getId().equals(book.getId())
        );
    }

    public List<Book> getBorrowedBooks(){
        return borrowedBooks.stream()
                .map(BookBorrow::getBook)
                .collect(Collectors.toList());
    }

    public List<Book> getBooksWithOutstandingExpiry(LocalDate date) {
        return borrowedBooks.stream()
                .filter(bookBorrow -> bookBorrow.getExpireAt().isBefore(date))
                .map(BookBorrow::getBook)
                .collect(Collectors.toList());
    }
}
