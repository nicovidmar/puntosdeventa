package com.cavallaro.kafka.pathfinder.dto;

import java.util.HashMap;
import java.util.Map;

public class SellingPointGraph {
    private final Map<Integer, Map<Integer, Double>> adjacencyList = new HashMap<>();

    public void addEdge(Integer pointA, Integer pointB, double cost) {
        adjacencyList.putIfAbsent(pointA, new HashMap<>());
        adjacencyList.putIfAbsent(pointB, new HashMap<>());

        adjacencyList.get(pointA).put(pointB, cost);
        adjacencyList.get(pointB).put(pointA, cost); // Si es bidireccional
    }

    public Map<Integer, Map<Integer, Double>> getAdjacencyList() {
        return adjacencyList;
    }
}

