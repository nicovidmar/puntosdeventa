package com.cavallaro.kafka;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.service.impl.SellingCostsCacheService;
import com.cavallaro.kafka.service.impl.SellingCostsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SellingCostsServiceImplTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private SellingCostsCacheService sellingCostsCacheService;

    @Mock
    private Cache cache;

    @InjectMocks
    private SellingCostsServiceImpl sellingCostsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(any())).thenReturn(cache);
    }

    @Test
    void testFindAll() {
        when(sellingCostsCacheService.findAll()).thenReturn(Collections.emptyList());
        when(cache.get("all")).thenReturn(null);

        ApiResponse<List<SellingCostDTO>> response = sellingCostsService.findAll();
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        verify(sellingCostsCacheService, times(1)).findAll();
    }

    @Test
    void testFindCostById() {
        when(sellingCostsCacheService.findId(anyInt(), anyInt())).thenReturn(Optional.of(new SellingCostDTO()));
        when(cache.get(any())).thenReturn(null);

        ApiResponse<Double> response = sellingCostsService.findCostById(1, 2);
        assertNotNull(response);
        assertEquals(0.0, response.getData());
        verify(sellingCostsCacheService, times(1)).findId(anyInt(), anyInt());
    }

    @Test
    void testSave() {
        SellingCostDTO dto = SellingCostDTO.builder().id(new SellingCostId()).build();
        when(sellingCostsCacheService.save(any(SellingCostDTO.class))).thenReturn(dto);
        when(cache.get(any())).thenReturn(null);

        ApiResponse<SellingCostDTO> response = sellingCostsService.save(dto);
        assertNotNull(response);
        assertEquals(dto, response.getData());
        verify(sellingCostsCacheService, times(1)).save(any(SellingCostDTO.class));
    }

    @Test
    void testRemove() {
        when(sellingCostsCacheService.remove(anyInt(), anyInt())).thenReturn(true);
        when(cache.get(any())).thenReturn(null);

        ApiResponse<Boolean> response = sellingCostsService.remove(1, 2);
        assertNotNull(response);
        assertTrue(response.getData());
        verify(sellingCostsCacheService, times(1)).remove(anyInt(), anyInt());
    }

    @Test
    void testUpdate() {
        when(sellingCostsCacheService.update(anyInt(), anyInt(), anyDouble())).thenReturn(Optional.empty());
        when(cache.get(any())).thenReturn(null);

        ApiResponse<Optional<SellingCostDTO>> response = sellingCostsService.update(1, 2, 100.0);
        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
        verify(sellingCostsCacheService, times(1)).update(anyInt(), anyInt(), anyDouble());
    }
}
