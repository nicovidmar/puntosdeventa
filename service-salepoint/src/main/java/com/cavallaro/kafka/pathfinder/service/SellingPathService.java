package com.cavallaro.kafka.pathfinder.service;


import com.cavallaro.kafka.dto.PathNameResult;

public interface SellingPathService {
    PathNameResult getCheapestPath(String algoritmo, Integer pointA, Integer pointB);
}
