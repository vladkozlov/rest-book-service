package com.epam.restbookservice.user.repository;

import com.epam.restbookservice.user.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
