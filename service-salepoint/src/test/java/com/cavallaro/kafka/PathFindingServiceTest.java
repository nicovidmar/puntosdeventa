package com.cavallaro.kafka;



import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.service.PathFindingService;
import com.cavallaro.kafka.pathfinder.service.PathFindingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class PathFindingServiceTest {

    @Mock
    private Map<String, PathFindingStrategy> strategyMap;

    @Mock
    private PathFindingStrategy mockStrategy;

    @InjectMocks
    private PathFindingService pathFindingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        strategyMap = new HashMap<>();
        strategyMap.put("dijkstra", mockStrategy);
        pathFindingService = new PathFindingService(strategyMap);
    }

    @Test
    public void testCalculatePathFinder_Success() {
        PathResult mockResult = new PathResult(List.of(), 0.0);
        when(mockStrategy.findShortestPath(anyInt(), anyInt())).thenReturn(mockResult);

        PathResult result = pathFindingService.calculatePathFinder("dijkstra", 1, 2);
        assertNotNull(result);
        assertEquals(mockResult, result);
    }

    @Test
    public void testCalculatePathFinder_AlgoritmoNoSoportado() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pathFindingService.calculatePathFinder("algoritmoInvalido", 1, 2);
        });

        String expectedMessage = "Algoritmo no soportado algoritmoInvalido";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
