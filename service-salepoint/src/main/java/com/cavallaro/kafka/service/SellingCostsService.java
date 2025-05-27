package com.cavallaro.kafka.service;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingCostDTO;

import java.util.List;
import java.util.Optional;

public interface SellingCostsService {

    ApiResponse<List<SellingCostDTO>> findAll();
    ApiResponse<Double> findCostById(Integer pointA, Integer pointB);
    ApiResponse<SellingCostDTO> save(SellingCostDTO sellingCostDTO);
    ApiResponse<Boolean> remove(Integer pointA, Integer pointB);
    ApiResponse<Optional<SellingCostDTO>> update(Integer pointA,Integer pointB, Double cost);
    ApiResponse<List<SellingCostDTO>> getReachableSellingPoints(Integer pointA);
}
