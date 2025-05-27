package com.cavallaro.kafka.pathfinder.service;




import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.dto.SellingPointGraph;

import java.util.*;


public class DijkstraAlgorithm {


    public static PathResult findShortestPath(SellingPointGraph graph, Integer start, Integer end) {
        Map<Integer, Map<Integer, Double>> adjacencyList = graph.getAdjacencyList();
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));



        for (Integer node : adjacencyList.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        priorityQueue.add(new Node(start, 0.0));

        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            Integer currentNode = current.id;

            if (currentNode.equals(end)) break;

            for (Map.Entry<Integer, Double> neighbor : adjacencyList.getOrDefault(currentNode, Collections.emptyMap()).entrySet()) {
                Integer neighborNode = neighbor.getKey();
                Double edgeWeight = neighbor.getValue();
                Double newDistance = distances.get(currentNode) + edgeWeight;

                if (newDistance < distances.get(neighborNode)) {
                    distances.put(neighborNode, newDistance);
                    previousNodes.put(neighborNode, currentNode);
                    priorityQueue.add(new Node(neighborNode, newDistance));
                }
            }
        }

        // Reconstruir el camino más corto
        List<Integer> path = new ArrayList<>();
        Integer step = end;
        while (step != null && previousNodes.containsKey(step)) {
            path.add(step);
            step = previousNodes.get(step);
        }
        path.add(start);
        Collections.reverse(path);

        if (path.size() == 1) {
            return new PathResult(Collections.emptyList(), 0.0);
        }

        return new PathResult(path, distances.get(end));
    }

    private static class Node {
        Integer id;
        Double cost;

        Node(Integer id, Double cost) {
            this.id = id;
            this.cost = cost;
        }
    }


}
