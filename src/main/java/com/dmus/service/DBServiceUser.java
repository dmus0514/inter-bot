package com.dmus.service;

import com.dmus.model.User;

import java.util.List;
import java.util.Optional;

public interface DBServiceUser {

    User saveUser(User user);

    Optional<User> getUser(long id);

    Optional<User> getUserByName(String name);

    List<User> findAll();
}
