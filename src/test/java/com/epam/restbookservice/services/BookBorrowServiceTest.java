package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Book;
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
import java.util.Optional;

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
}
