package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.repositories.BookBorrowRepository;
import com.epam.restbookservice.repositories.BookRepository;
import com.epam.restbookservice.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookBorrowServiceTest {

    @InjectMocks
    private BookBorrowService underTest;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookBorrowRepository bookBorrowRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ExpiryService expiryService;
    @Mock
    private UserService userService;

    @Test
    void borrowABookTest() {
        final String date = "2020-02-09";
        long bookId = 5L;
        var book = new Book();
        book.setId(bookId);

        var bookOptional = Optional.of(book);
        when(bookRepository.findById(bookId)).thenReturn(bookOptional);
        when(expiryService.isBorrowDateValid(LocalDate.parse(date))).thenReturn(true);
        when(bookBorrowRepository.existsBookBorrowByBook(book)).thenReturn(false);
        when(userService.getCurrentUser()).thenReturn(Optional.of(new User("a", "b", "c", "d")));

        underTest.borrowABook(bookId, LocalDate.parse(date));

        verify(userRepository).save(any());
    }

    @Test
    void getAllBorrowsTest() {
        var bookBorrow = new BookBorrow(new User(), new Book(), LocalDate.now());
        when(bookBorrowRepository.findAll()).thenReturn(List.of(bookBorrow));

        assertThat(underTest.getAllBorrows()).hasSize(1);
    }

    @Test
    void returnABook() {
        var bookId = 3L;

        var book = new Book();
        book.setId(bookId);
        var bookOpt = Optional.of(book);
        var bookBorrowList = new ArrayList<BookBorrow>();
        bookBorrowList.add(new BookBorrow(book, LocalDate.now()));
        var user = new User();
        user.setBorrowedBooks(bookBorrowList);
        var userOpt = Optional.of(user);

        when(bookRepository.findById(bookId)).thenReturn(bookOpt);
        when(userService.getCurrentUser()).thenReturn(userOpt);

        underTest.returnABook(bookId);
        verify(userRepository).save(any());
    }

    @Test
    void addBorrow() {
        final long bookId = 4L;
        final long userId = 4L;
        when(expiryService.isBorrowDateValid(any())).thenReturn(true);
        final User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        final Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        final LocalDate tillDate = LocalDate.now().plusDays(3);
        underTest.addBorrow(bookId, userId, tillDate);
        verify(bookBorrowRepository).save(new BookBorrow(user, book, tillDate));
    }

    @Test
    void editBookBorrow() {
        final long bookBorrowId = 4L;
        final long userId = 4L;
        final long bookId = 4L;
        when(bookBorrowRepository.findById(bookBorrowId)).thenReturn(Optional.of(new BookBorrow()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));
        when(expiryService.isBorrowDateValid(any())).thenReturn(true);

        underTest.editBookBorrow(bookBorrowId, userId, bookId, LocalDate.now().plusDays(4));

        verify(bookBorrowRepository).save(any());
    }

}
