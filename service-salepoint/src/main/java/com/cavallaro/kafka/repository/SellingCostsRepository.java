package com.cavallaro.kafka.repository;


import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SellingCostsRepository extends MongoRepository<SellingCost, SellingCostId> {

    boolean existsByIdPointA(Integer pointA);

    boolean existsByIdPointB(Integer pointB);
}

