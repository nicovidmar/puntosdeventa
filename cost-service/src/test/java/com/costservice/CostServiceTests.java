package com.costservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import com.costservice.dto.PointOfSaleDTO;
import com.costservice.dto.ShortestPathResponse;
import com.costservice.service.CostService;
import com.costservice.webclient.WebClientPosService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CostServiceTests {

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private WebClientPosService posService;

    private CostService costService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOps);
        costService = new CostService(redisTemplate, posService);
    }

    @Test
    void testSave_OK() {
        int idA = 1, idB = 2, cost = 10;

        PointOfSaleDTO posA = new PointOfSaleDTO(idA, "POS A");
        PointOfSaleDTO posB = new PointOfSaleDTO(idB, "POS B");

        when(posService.findById(idA)).thenReturn(posA);
        when(posService.findById(idB)).thenReturn(posB);
        when(hashOps.hasKey("costs", "1_2")).thenReturn(false);

        boolean result = costService.save(idA, idB, cost);

        assertTrue(result);
        verify(hashOps).put("costs", "1_2", cost);
        verify(hashOps).put("costs", "2_1", cost);
    }

    @Test
    void testSave_NegativeCost() {
        int idA = 1, idB = 2, cost = -5;
        when(posService.findById(idA)).thenReturn(new PointOfSaleDTO(idA, "POS A"));
        when(posService.findById(idB)).thenReturn(new PointOfSaleDTO(idB, "POS B"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> costService.save(idA, idB, cost));

        assertEquals("El costo no puede ser negativo", ex.getMessage());
    }

    @Test
    void testSave_CostNotZeroFromSamePoint() {
        int idA = 1, cost = 5;
        when(posService.findById(idA)).thenReturn(new PointOfSaleDTO(idA, "POS A"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> costService.save(idA, idA, cost));

        assertEquals("Costo de un punto al mismo punto no puede ser distinto de 0", ex.getMessage());
    }

    @Test
    void testDeleteCost() {
        int idA = 1, idB = 2;
        when(posService.findById(idA)).thenReturn(new PointOfSaleDTO(idA, "POS A"));
        when(posService.findById(idB)).thenReturn(new PointOfSaleDTO(idB, "POS B"));

        costService.deleteCost(idA, idB);

        verify(hashOps).delete("costs", "1_2");
        verify(hashOps).delete("costs", "2_1");
    }

    @Test
    void testGetDirectConnectionsFrom() {
        int id = 1;
        when(posService.findById(id)).thenReturn(new PointOfSaleDTO(id, "POS A"));

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1_2", 10);
        entries.put("1_3", 5);
        entries.put("2_3", 7);

        when(hashOps.entries("costs")).thenReturn(entries);

        Map<Integer, Integer> result = costService.getDirectConnectionsFrom(id);

        assertEquals(2, result.size());
        assertEquals(10, result.get(2));
        assertEquals(5, result.get(3));
    }

    @Test
    void testGetShortestPath() {
        int idA = 1, idB = 3;

        when(posService.findById(idA)).thenReturn(new PointOfSaleDTO(idA, "POS A"));
        when(posService.findById(2)).thenReturn(new PointOfSaleDTO(2, "POS B"));
        when(posService.findById(idB)).thenReturn(new PointOfSaleDTO(idB, "POS C"));

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1_2", 1);
        entries.put("2_3", 1);
        entries.put("1_3", 10); // camino mas largo

        when(hashOps.entries("costs")).thenReturn(entries);

        ShortestPathResponse response = costService.getShortestPath(idA, idB);

        assertEquals("POS A", response.getFrom());
        assertEquals("POS C", response.getTo());
        assertEquals(2, response.getTotalCost());
        assertEquals(List.of("POS A", "POS B", "POS C"), response.getPath());
    }

    @Test
    void testGetShortestPathNoConnectionThrowsException() {
        int idA = 1, idB = 4;

        when(posService.findById(idA)).thenReturn(new PointOfSaleDTO(idA, "POS A"));
        when(posService.findById(idB)).thenReturn(new PointOfSaleDTO(idB, "POS D"));

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1_2", 1);
        entries.put("2_3", 1);

        when(hashOps.entries("costs")).thenReturn(entries);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> costService.getShortestPath(idA, idB));

        assertEquals("No hay camino entre los puntos de venta", ex.getMessage());
    }
}
