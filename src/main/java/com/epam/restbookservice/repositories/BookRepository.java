package com.epam.restbookservice.repositories;

import com.epam.restbookservice.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsBookById(Long bookId);
}
