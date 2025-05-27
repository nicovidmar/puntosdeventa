package com.cavallaro.kafka.repositories;

import com.cavallaro.kafka.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;



public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
