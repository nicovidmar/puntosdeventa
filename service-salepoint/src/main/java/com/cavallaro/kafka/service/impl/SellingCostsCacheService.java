package com.cavallaro.kafka.service.impl;


import com.cavallaro.kafka.dto.SellingCostDTO;

import java.util.List;
import java.util.Optional;

public interface SellingCostsCacheService {
    List<SellingCostDTO> findAll();
    Optional<SellingCostDTO> findId(Integer pointA, Integer pointB);
    SellingCostDTO save(SellingCostDTO sellingCostDTO);
    boolean remove(Integer pointA, Integer pointB);
    Optional<SellingCostDTO> update(Integer pointA,Integer pointB, Double cost);
}
