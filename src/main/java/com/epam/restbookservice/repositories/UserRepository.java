package com.epam.restbookservice.repositories;

import com.epam.restbookservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("select u from User u  join fetch u.roles where u.username = :name")
    Optional<User> findByUsername(String name);

    boolean existsByUsername(String username);

}
