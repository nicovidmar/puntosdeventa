package com.cavallaro.kafka;



import com.cavallaro.kafka.dto.PathNameResult;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.service.PathFindingService;
import com.cavallaro.kafka.pathfinder.service.SellingPathServiceImpl;
import com.cavallaro.kafka.service.SellingPointCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SellingPathServiceImplTest {

    @Mock
    private SellingPointCacheService sellingPointCacheService;

    @Mock
    private PathFindingService pathFindingService;

    @InjectMocks
    private SellingPathServiceImpl sellingPathService;

    private SellingPointDTO puntoA;
    private SellingPointDTO puntoB;

    @BeforeEach
    public void setUp() {
        puntoA = new SellingPointDTO(1, "Punto A");
        puntoB = new SellingPointDTO(2, "Punto B");

        lenient().when(sellingPointCacheService.getCachedSellingPoints()).thenReturn(Arrays.asList(puntoA, puntoB));
    }

    @Test
    public void testGetCheapestPath_Success() {
        PathResult pathResult = new PathResult(Arrays.asList(1, 2), 100.0);
        when(pathFindingService.calculatePathFinder("algoritmo", 1, 2)).thenReturn(pathResult);

        PathNameResult result = sellingPathService.getCheapestPath("algoritmo", 1, 2);

        assertNotNull(result);
        assertEquals(Arrays.asList("Punto A", "Punto B"), result.path());
        assertEquals(100.0, result.totalCost());

        verify(sellingPointCacheService,times(2)).getCachedSellingPoints();
        verify(pathFindingService).calculatePathFinder("algoritmo", 1, 2);
    }

    @Test
    public void testGetCheapestPath_NoPathFound() {
        PathResult pathResult = new PathResult(Collections.emptyList(), 0.0);
        when(pathFindingService.calculatePathFinder("algoritmo", 1, 2)).thenReturn(pathResult);

        SellingCostsServiceException exception = assertThrows(SellingCostsServiceException.class, () ->
                sellingPathService.getCheapestPath("algoritmo", 1, 2)
        );

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getCode());
        assertEquals("No existe un camino entre los puntos 1 y 2", exception.getMessage());
    }

    @Test
    public void testGetCheapestPath_PointNameNotFound() {
        PathResult pathResult = new PathResult(Arrays.asList(1, 3), 100.0);
        when(pathFindingService.calculatePathFinder("algoritmo", 1, 3)).thenReturn(pathResult);

        PathNameResult result = sellingPathService.getCheapestPath("algoritmo", 1, 3);

        assertNotNull(result);
        assertEquals(Arrays.asList("Punto A", "No se encontro Punto de venta"), result.path());
        assertEquals(100.0, result.totalCost());
    }
}