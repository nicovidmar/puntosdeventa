package com.cavallaro.kafka.repositories;

import java.util.Optional;

import com.cavallaro.kafka.model.Role;
import org.springframework.data.repository.CrudRepository;



public interface RoleRepository extends CrudRepository<Role, Long>{
    Optional<Role> findByName(String name);
}
