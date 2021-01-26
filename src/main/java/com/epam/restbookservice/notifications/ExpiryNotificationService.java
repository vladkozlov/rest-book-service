package com.epam.restbookservice.notifications;


import com.epam.restbookservice.domain.BookBorrow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExpiryNotificationService extends NotificationService {

    void notifyExpiry(List<BookBorrow> bookBorrow);
}
