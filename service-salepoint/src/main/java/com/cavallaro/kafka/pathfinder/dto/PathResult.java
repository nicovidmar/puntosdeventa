package com.cavallaro.kafka.pathfinder.dto;


import lombok.Getter;

import java.util.List;


@Getter
public class PathResult {
    private final List<Integer> path;
    private final double totalCost;


    public PathResult(List<Integer> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

}
