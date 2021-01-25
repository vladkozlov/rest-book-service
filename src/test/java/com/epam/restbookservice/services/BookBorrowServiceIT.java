package com.epam.restbookservice.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.repositories.BookRepository;
import com.epam.restbookservice.repositories.UserRepository;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BookBorrowServiceIT {

    @Autowired
    private BookBorrowService service;
    @Autowired
    private BookRepository bookRepo;
    @MockBean
    private UserService userService;
    @Autowired
    private UserRepository userRepo;

    private long id;
    private User user;

    @BeforeAll
    public static void startH2Console() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @BeforeEach
    void setUp() {
        var book = new Book();
        book.setISBN("1235ABC");
        book.setTitle("Title");
        bookRepo.save(book);
        id = book.getId();
        var userOptional = userRepo.findByUsername("librarian");
        user = userOptional.get();
        Mockito.when(userService.getCurrentUser())
                .thenReturn(userOptional);
    }

    @AfterEach
    void cleanUp() throws InterruptedException {
//        Thread.sleep(1000000000L);
    }

    @Test
    void returnABook() {
        assertNotNull(service);
        service.borrowABook(id, LocalDate.now());
        // assert
        System.out.println(user.getBorrowedBooks()
                .stream()
                .filter(book -> id == book.getId())
                .findFirst());
        service.returnABook(id);
        // assert
        System.out.println(user.getBorrowedBooks()
                .stream()
                .filter(book -> id == book.getId())
                .findFirst());
    }
}