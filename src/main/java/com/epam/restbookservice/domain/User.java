package com.epam.restbookservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

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

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
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

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
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

    public void borrowBook(BookBorrow bookBorrow) {
        borrowedBooks.add(bookBorrow);
        bookBorrow.setUser(this);
    }

    public void borrowBook(Book book, LocalDate expireDate) {
        borrowedBooks.add(new BookBorrow(this, book, expireDate));
    }

    public void returnBook(BookBorrow bookBorrow) {
        borrowedBooks.remove(bookBorrow);
        bookBorrow.setUser(null);
    }

//    public void returnBook(Book book) {
//        borrowedBooks.removeIf(bookBorrow ->
//                book.getId().equals( bookBorrow.getBook().getId() )
//        );
//    }

    public List<Book> getBooks(){
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
