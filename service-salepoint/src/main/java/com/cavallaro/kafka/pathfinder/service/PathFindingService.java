package com.cavallaro.kafka.pathfinder.service;

import com.cavallaro.kafka.pathfinder.dto.PathResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class PathFindingService {
    private final Map<String, PathFindingStrategy> strategy;

    public PathResult calculatePathFinder(String algoritmo, Integer pointA, Integer pointB){
        PathFindingStrategy strategy1 = strategy.get(algoritmo);
        if  (strategy1 ==null){
            throw new IllegalArgumentException("Algoritmo no soportado "  + algoritmo) ;
        }

        return  strategy1.findShortestPath(pointA, pointB);

    }

}
