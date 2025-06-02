package com.dmus.repository;

import com.dmus.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UsersRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByTgUsername(String tgUsername);
}
