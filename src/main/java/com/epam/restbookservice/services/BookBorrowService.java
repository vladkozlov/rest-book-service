package com.epam.restbookservice.services;

import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.exceptions.BookIsBorrowedException;
import com.epam.restbookservice.exceptions.BookNotFoundException;
import com.epam.restbookservice.exceptions.ExpiryDateInvalidException;
import com.epam.restbookservice.repositories.BookBorrowRepository;
import com.epam.restbookservice.repositories.BookRepository;
import com.epam.restbookservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.epam.restbookservice.services.UserService.getCurrentAccountUsername;

@Service
public class BookBorrowService {

    private final UserService userService;
    private final BookBorrowRepository bookBorrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ExpiryService expiryService;

    public BookBorrowService(UserService userService, BookBorrowRepository bookBorrowRepository, BookRepository bookRepository, UserRepository userRepository, ExpiryService expiryService) {
        this.userService = userService;
        this.bookBorrowRepository = bookBorrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.expiryService = expiryService;
    }

    public void borrowABook(Long bookId, LocalDate tillDate) {

        validateExpiryDate(tillDate);

        var bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id %d not found", bookId));
        }

        var book = bookOptional.get();

        if (bookBorrowRepository.existsBookBorrowByBook(book)) {
            throw new BookIsBorrowedException(String.format("Book with id %d already borrowed", bookId));
        }

        userService.getCurrentUser()
                .ifPresent(user -> {
                    var bookBorrow = new BookBorrow(user, book, tillDate);

                    user.borrowBook(bookBorrow);
                    userRepository.save(user);
                });
    }

    public void returnABook(Long bookId) {

        var bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            throw new BookNotFoundException(String.format("Book with id %d not found", bookId));
        }

        var book = bookOptional.get();
        userService.getCurrentUser()
                .ifPresent(user -> {
                    var bookBorrow =
                            user.getBorrowedBooks()
                                    .stream()
                                    .filter(bb -> book.equals(bb.getBook()))
                                    .findFirst();

                    if (bookBorrow.isPresent()) {
                        user.returnABook(bookBorrow.get());
                        userRepository.save(user);
                    }
                });
    }

    public List<BookBorrow> getAllBorrowedBooks() {
        return bookBorrowRepository.findAll();
    }


    public List<BookBorrow> getAllExpiringBorrowsByDays(Long days) {
        var expiryDate = LocalDate.now().plusDays(days);
        return bookBorrowRepository.findAllWithExpiringDateEqual(expiryDate);
    }

    public void extendBorrowTime(Long bookId, LocalDate expiryDate) {

        validateExpiryDate(expiryDate);

        if (!bookRepository.existsBookById(bookId)) {
            throw new BookNotFoundException(String.format("Book with id %d not found", bookId));
        }

        bookBorrowRepository
                .getBookBorrowByBookIdAndUsername(bookId, getCurrentAccountUsername())
                .ifPresent(bookBorrow -> {
                    bookBorrow.setExpireAt(expiryDate);
                    bookBorrowRepository.save(bookBorrow);
                });
    }

    private void validateExpiryDate(LocalDate expiryDate) {
        if (!expiryService.isBorrowDateValid(expiryDate)) {
            throw new ExpiryDateInvalidException("Expiry date is invalid. Borrow date is limited to a max 1 month.");
        }
    }
}
