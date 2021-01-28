package com.epam.restbookservice.notifications;

import org.springframework.stereotype.Component;

@Component
public interface NotificationService {

    void doNotify();

}
