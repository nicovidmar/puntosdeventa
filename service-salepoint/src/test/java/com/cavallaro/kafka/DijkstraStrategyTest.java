package com.cavallaro.kafka;



import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.pathfinder.dto.PathResult;
import com.cavallaro.kafka.pathfinder.service.DijkstraStrategy;
import com.cavallaro.kafka.service.impl.SellingCostsCacheService;
import com.cavallaro.kafka.validation.SellingPathValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DijkstraStrategyTest {

    @Mock
    private SellingPathValidator sellingPathValidator;

    @Mock
    private SellingCostsCacheService sellingCostsCacheService;

    @InjectMocks
    private DijkstraStrategy dijkstraStrategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindShortestPath_Success() {
        Integer start = 1;
        Integer end = 5;

        List<SellingCostDTO> mockCosts = Arrays.asList(
                SellingCostDTO.builder().id(new SellingCostId(1,2)).cost(2.0).build(),
                SellingCostDTO.builder().id(new SellingCostId(2,5)).cost(6.0).build(),
                SellingCostDTO.builder().id(new SellingCostId(1,3)).cost(3.0).build()
        );

        when(sellingCostsCacheService.findAll()).thenReturn(mockCosts);

        PathResult result = dijkstraStrategy.findShortestPath(start, end);

        verify(sellingPathValidator, times(1)).validate(start, end);
        verify(sellingCostsCacheService, times(1)).findAll();

        assertEquals(Arrays.asList(1, 2, 5), result.getPath(), "El camino resultante no es el esperado.");
        assertEquals(8, result.getTotalCost(), "El costo total no es el esperado.");
    }

    @Test
    public void testFindShortestPath_NoPathFound() {
        Integer start = 1;
        Integer end = 10;

        List<SellingCostDTO> mockCosts = Arrays.asList(
                SellingCostDTO.builder().id(new SellingCostId(1, 2)).cost(2.0).build(),
                SellingCostDTO.builder().id(new SellingCostId(2, 5)).cost(6.0).build(),
                SellingCostDTO.builder().id(new SellingCostId(1, 3)).cost(3.0).build()
        );

        when(sellingCostsCacheService.findAll()).thenReturn(mockCosts);

        PathResult result = dijkstraStrategy.findShortestPath(start, end);

        assertEquals(0, result.getPath().size(), "El camino resultante debería estar vacío.");
        assertEquals(0, result.getTotalCost(), "El costo total debería ser 0.");
    }
}
