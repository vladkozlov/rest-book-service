package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.dtos.BookDTO;
import com.epam.restbookservice.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    BookService underTest;

    @Mock
    BookRepository bookRepository;

    @Test
    void saveBook() {
        BookDTO bookDTO = new BookDTO();
        String isbn = "12345";
        bookDTO.setISBN(isbn);

        Book book = new Book();
        book.setISBN(isbn);
        when(bookRepository.save(any())).thenReturn(book);

        Book savedBook = underTest.saveBook(bookDTO);

        assertThat(savedBook.getISBN(), is(isbn));
    }

    @Test
    void getBooks() {
        Book book = new Book();
        String isbn = "12345";
        book.setISBN(isbn);

        List<Book> bookList = Collections.singletonList(book);
        when(bookRepository.findAll()).thenReturn(bookList);

        List<Book> books = underTest.getBooks();
        assertThat(books, is(bookList));
    }

    @Test
    void getBook() {
        long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> optionalBook = underTest.getBook(bookId);

        assertThat(optionalBook.isPresent(), is(true));
        assertThat(optionalBook.get().getId(), is(bookId));
    }

    @Test
    void updateBook() {
        long bookId = 1L;
        BookDTO bookDTO = new BookDTO();
        Book book = new Book();
        book.setISBN("123");

        Book updatedBook = new Book();
        updatedBook.setISBN("ABC");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(updatedBook);

        Optional<Book> optionalBook = underTest.updateBook(bookId, bookDTO);

        assertThat(optionalBook.get(), is(updatedBook));
    }

    @Test
    void updateBook_notFoundId_shouldReturnEmpty() {
        long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Optional<Book> optionalBook = underTest.updateBook(bookId, new BookDTO());

        assertFalse(optionalBook.isPresent());
    }

    @Test
    void deleteBook() {
        long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        underTest.deleteBook(bookId);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_shouldNotDeleteNotfoundBook() {
        long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        underTest.deleteBook(bookId);

        verify(bookRepository, never()).delete(book);
    }
}