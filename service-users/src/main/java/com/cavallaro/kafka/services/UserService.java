package com.cavallaro.kafka.services;

import com.cavallaro.kafka.model.User;

import java.util.Optional;



public interface UserService {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Iterable<User> findAll();

    User save(User user);
    Optional<User> update(User user, Long id);

    void delete(Long id);
}
