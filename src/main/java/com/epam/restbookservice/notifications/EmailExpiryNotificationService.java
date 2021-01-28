package com.epam.restbookservice.notifications;

import com.epam.restbookservice.domain.BookBorrow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EmailExpiryNotificationService implements ExpiryNotificationService {

    private String message = "";

    @Override
    public void notifyExpiry(List<BookBorrow> bookBorrowList) {
        bookBorrowList
                .stream()
                .collect(Collectors.groupingBy(bb -> bb.getUser().getUsername()))
                .forEach((username, bookBorrowListForUser) -> {
                    message = buildExpiryNotificationMessage(username, bookBorrowListForUser);

                    doNotify();
                });
    }

    private String buildExpiryNotificationMessage(String username, List<BookBorrow> bookBorrowListForUser) {
        var messageBuilder = new StringBuilder();
        var counter = new AtomicInteger(0);
        messageBuilder.append("\nDear ");
        messageBuilder.append(username);
        messageBuilder.append("!\n");
        messageBuilder.append("You have the next books which borrow time expire soon:\n");
        messageBuilder.append(" No. | Title | ISBN | Expiry date\n");
        messageBuilder.append(bookBorrowListForUser
                .stream()
                .map(bookBorrow -> {
                    var book = bookBorrow.getBook();
                    return String.format("%d | %s | %s | %s", counter.incrementAndGet(), book.getTitle(), book.getISBN(), bookBorrow.getExpireAt());
                })
                .collect(Collectors.joining(","))
        );
        return messageBuilder.toString();
    }

    @Override
    public void doNotify() {
        log.info(message);
    }
}
