package com.cavallaro.kafka.pathfinder.service;


import com.cavallaro.kafka.pathfinder.dto.PathResult;

public interface PathFindingStrategy {

    PathResult findShortestPath(Integer start, Integer end);
}
