package com.cavallaro.kafka.repository;

import com.cavallaro.kafka.model.SellingPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface SellingPointsRepository extends MongoRepository<SellingPoint, Integer> {
    Optional<SellingPoint> findByName(String name);

}

