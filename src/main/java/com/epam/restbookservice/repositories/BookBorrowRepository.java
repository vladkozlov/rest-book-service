package com.epam.restbookservice.repositories;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long> {

    @EntityGraph(
            type = EntityGraph.EntityGraphType.LOAD,
            attributePaths = {"book"}
    )
    List<BookBorrow> findAll();
    boolean existsBookBorrowByBook(Book book);
    @Query("select bb from BookBorrow bb join fetch bb.user where bb.expireAt = :date")
    List<BookBorrow> findAllWithExpiringDateEqual(LocalDate date);
}
