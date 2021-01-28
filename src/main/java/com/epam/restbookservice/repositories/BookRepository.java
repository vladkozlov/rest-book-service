package com.epam.restbookservice.repositories;

import com.epam.restbookservice.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    boolean existsBookById(Long bookId);

    List<Book> findByTitleContaining(String title);

}
