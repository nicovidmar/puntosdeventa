package com.posservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.*;

import com.posservice.dto.PointOfSaleRequest;
import com.posservice.entity.PointOfSale;
import com.posservice.service.PointOfSaleService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointOfSaleServiceTests {

    @Mock
    private RedisTemplate<String, PointOfSale> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private ValueOperations<String, PointOfSale> valueOps;

    @InjectMocks
    private PointOfSaleService pointOfSaleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void testFindAll_OK() {
        PointOfSale pos1 = new PointOfSale(1, "A");
        PointOfSale pos2 = new PointOfSale(2, "B");

        when(hashOps.values("POS")).thenReturn(List.of(pos1, pos2));

        List<PointOfSale> result = pointOfSaleService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testSave_OK() {
        PointOfSale pos = new PointOfSale(1, "pos");

        when(hashOps.values("POS")).thenReturn(List.of()); // no hay duplicados
        when(valueOps.increment("pos:id:seq")).thenReturn(1L);

        PointOfSale saved = pointOfSaleService.save(pos);

        assertEquals(1, saved.getId());
        verify(hashOps).put("POS", "1", saved);
    }

    @Test
    void testSave_POSAlreadyExist() {
        PointOfSale existing = new PointOfSale(1, "pos1");
        PointOfSale newPos = new PointOfSale(2, "pos1");

        when(hashOps.values("POS")).thenReturn(List.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pointOfSaleService.save(newPos));

        assertEquals("Ya existe un PointOfSale con el nombre pos1", ex.getMessage());
    }

    @Test
    void testUpdate_OK() {
        int id = 1;
        PointOfSale existing = new PointOfSale(id, "pos1");
        PointOfSaleRequest request = new PointOfSaleRequest("pos2");

        when(hashOps.get("POS", String.valueOf(id))).thenReturn(existing);
        when(hashOps.values("POS")).thenReturn(List.of(existing));

        PointOfSale updated = pointOfSaleService.update(id, request);

        assertEquals("pos2", updated.getName());
        verify(hashOps).put("POS", String.valueOf(id), updated);
    }

    @Test
    void testUpdate_POSAlreadyExist() {
        int id = 1;
        PointOfSale pos = new PointOfSale(id, "pos1");
        PointOfSale existing = new PointOfSale(2, "pos2");
        PointOfSaleRequest request = new PointOfSaleRequest("pos2");

        when(hashOps.get("POS", String.valueOf(id))).thenReturn(pos);
        when(hashOps.values("POS")).thenReturn(List.of(pos, existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pointOfSaleService.update(id, request));

        assertEquals("Ya existe un PointOfSale con el nombre pos2", ex.getMessage());
    }

    @Test
    void testDelete_OK() {
        PointOfSale pos = new PointOfSale(1, "pos");

        when(hashOps.get("POS", "1")).thenReturn(pos);

        pointOfSaleService.delete(1);
        verify(hashOps).delete("POS", "1");
    }

    @Test
    void testFindById_OK() {
        PointOfSale pos = new PointOfSale(5, "pos");
        when(hashOps.get("POS", "5")).thenReturn(pos);

        PointOfSale found = pointOfSaleService.findById(5);
        assertEquals("pos", found.getName());
    }

    @Test
    void testFindById_NotFound() {
        when(hashOps.get("POS", "9")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pointOfSaleService.findById(9));

        assertEquals("No existe PointOfSale con ID 9", ex.getMessage());
    }
}
