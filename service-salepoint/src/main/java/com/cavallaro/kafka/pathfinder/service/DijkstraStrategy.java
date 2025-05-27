package com.cavallaro.kafka.pathfinder.service;


import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.dto.SellingPointGraph;
import com.cavallaro.kafka.service.impl.SellingCostsCacheService;
import com.cavallaro.kafka.validation.SellingPathValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dijkstra")
public class DijkstraStrategy implements PathFindingStrategy {

    private  final SellingPathValidator sellingPathValidator;
    private final SellingCostsCacheService sellingCostsCacheService;

    @Autowired
    public DijkstraStrategy(SellingPathValidator sellingPathValidator,
                            SellingCostsCacheService sellingCostsCacheService) {
        this.sellingPathValidator = sellingPathValidator;
        this.sellingCostsCacheService = sellingCostsCacheService;
    }
    @Override
    public PathResult findShortestPath(Integer start, Integer end) {
        sellingPathValidator.validate(start, end);

        List<SellingCostDTO> allCosts = sellingCostsCacheService.findAll();
        SellingPointGraph graph = new SellingPointGraph();

        // Construcción del grafo desde los datos del caché
        allCosts.forEach(cost -> graph.addEdge(
                cost.getId().getPointA(),
                cost.getId().getPointB(),
                cost.getCost()
        ));
        return DijkstraAlgorithm.findShortestPath(graph,start, end);
    }
}
