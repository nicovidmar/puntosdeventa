package com.cavallaro.kafka;



import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.dto.SellingPointGraph;
import com.cavallaro.kafka.pathfinder.service.DijkstraAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DijkstraAlgorithmTest {

    @Mock
    private SellingPointGraph graph;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockGraph();
    }

    private void mockGraph() {
        when(graph.getAdjacencyList()).thenReturn(Map.of(
                1, Map.of(2, 2.0, 3, 3.0, 4, 11.0),
                2, Map.of(1, 2.0, 3, 5.0, 4, 10.0, 5, 14.0),
                3, Map.of(1, 3.0, 2, 5.0, 8, 10.0),
                4, Map.of(1, 11.0, 2, 10.0, 5, 5.0, 6, 6.0),
                5, Map.of(2, 14.0, 4, 5.0, 8, 30.0, 10, 5.0),
                6, Map.of(4, 6.0, 7, 32.0),
                7, Map.of(6, 32.0, 10, 5.0),
                8, Map.of(3, 10.0, 5, 30.0, 9, 11.0),
                9, Map.of(8, 11.0),
                10, Map.of(5, 5.0, 7, 5.0)
        ));
    }


    @Test
    public void testFindShortestPath_From1To5_ShouldReturnCorrectPathAndCost() {
        PathResult result = DijkstraAlgorithm.findShortestPath(graph, 1, 5);
        assertEquals(16.0, result.getTotalCost());
        assertEquals(List.of(1, 2, 5), result.getPath());
    }


    @Test
    public void testFindShortestPath_From1To10_ShouldReturnCorrectPathAndCost() {
        PathResult result = DijkstraAlgorithm.findShortestPath(graph, 1, 10);
        assertEquals(21.0, result.getTotalCost());
        assertEquals(List.of(1, 2, 5, 10), result.getPath());
    }

}

