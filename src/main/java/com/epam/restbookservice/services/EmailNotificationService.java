package com.epam.restbookservice.services;

import com.epam.restbookservice.notifications.ExpiryNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificationService {

    @Value("${book.borrow.email.notification.period.days:3}")
    private Long emailNotificationPeriodInDays;

    private final BookBorrowService bookBorrowService;
    private final ExpiryNotificationService expiryNotificationService;

    public EmailNotificationService(BookBorrowService bookBorrowService, ExpiryNotificationService expiryNotificationService) {
        this.bookBorrowService = bookBorrowService;
        this.expiryNotificationService = expiryNotificationService;
    }

    @Scheduled(fixedRate = 86400000) // 1 day
    public void scheduledLookupForExpiringBookBorrowsTask() {
        var bookBorrows = bookBorrowService.getAllExpiringBorrowsByDays(this.emailNotificationPeriodInDays);

        expiryNotificationService.notifyExpiry(bookBorrows);
    }

}
