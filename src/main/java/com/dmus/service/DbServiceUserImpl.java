package com.dmus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dmus.model.User;
import com.dmus.repository.UsersRepository;
import com.dmus.sessionmanager.TransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DbServiceUserImpl implements DBServiceUser {
    private static final Logger log = LoggerFactory.getLogger(DbServiceUserImpl.class);

    private final TransactionManager transactionManager;
    private final UsersRepository usersRepository;

    public DbServiceUserImpl(TransactionManager transactionManager, UsersRepository usersRepository) {
        this.transactionManager = transactionManager;
        this.usersRepository = usersRepository;
    }

    @Override
    public User saveUser(User user) {
        return transactionManager.doInTransaction(() -> {
            var savedUser = usersRepository.save(user);
            log.info("saved user: {}", savedUser);
            return savedUser;
        });
    }

    @Override
    public Optional<User> getUser(long id) {
        var userOptional = usersRepository.findById(id);
        log.info("user: {}", userOptional);
        return userOptional;
    }

    @Override
    public Optional<User> getUserByName(String name) {
        var userOptional = usersRepository.findByTgUsername(name);
        log.info("user: {}", userOptional);
        return userOptional;
    }

    @Override
    public List<User> findAll() {
        var userList = new ArrayList<User>();
        usersRepository.findAll().forEach(userList::add);
        log.info("userList:{}", userList);
        return userList;
    }
}
