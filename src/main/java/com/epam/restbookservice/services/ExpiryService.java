package com.epam.restbookservice.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpiryService {

    public boolean isBorrowDateValid(LocalDate expiryDate) {
        return expiryDate.isBefore(LocalDate.now().plusMonths(1));
    }

}
