package com.cavallaro.kafka.cache;


import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.model.SellingCostId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SellingCostsCacheTest {

    private static final String CACHE_NAME = "sellingCosts";

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private SellingCostsCache sellingCostsCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    }

    @Test
    void shouldReturnCachedSellingCosts() {
        List<SellingCostDTO> cachedList = new ArrayList<>();
        cachedList.add(SellingCostDTO.builder().id(new SellingCostId(1,1)).cost(100.0).build());

        when(cache.get("all", List.class)).thenReturn(cachedList);

        List<SellingCostDTO> result = sellingCostsCache.getAllCached();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getCost());
    }

    @Test
    void shouldSaveSellingCostInCache() {
        List<SellingCostDTO> cachedList = new ArrayList<>();
        when(cache.get("all", List.class)).thenReturn(cachedList);

        SellingCostDTO newCost = SellingCostDTO.builder().id(new SellingCostId(2,2)).cost(200.0).build();
        sellingCostsCache.save(newCost);

        assertTrue(cachedList.contains(newCost));
        verify(cache).put(eq("all"), eq(cachedList));
    }

    @Test
    void shouldRemoveSellingCostFromCache() {
        SellingCostId idToRemove = new SellingCostId(1,1);
        List<SellingCostDTO> cachedList = new ArrayList<>();
        cachedList.add(SellingCostDTO.builder().id(new SellingCostId(1,1)).cost(100.0).build());
        cachedList.add(SellingCostDTO.builder().id(new SellingCostId(2,2)).cost(200.0).build());

        when(cache.get("all", List.class)).thenReturn(cachedList);

        sellingCostsCache.remove(idToRemove);

        assertEquals(1, cachedList.size());
        assertFalse(cachedList.stream().anyMatch(sp -> Objects.equals(sp.getId(), idToRemove)));
        verify(cache).put(eq("all"), eq(cachedList));
    }

    @Test
    void shouldUpdateSellingCostInCache() {
        SellingCostId idToUpdate = new SellingCostId(1,1);
        List<SellingCostDTO> cachedList = new ArrayList<>();
        cachedList.add(SellingCostDTO.builder().id(new SellingCostId(1,1)).cost(100.0).build());
        cachedList.add(SellingCostDTO.builder().id(new SellingCostId(2,2)).cost(200.0).build());



        when(cache.get("all", List.class)).thenReturn(cachedList);

        SellingCostDTO updatedCost = SellingCostDTO.builder().id(new SellingCostId(1,1)).cost(300.0).build();
        sellingCostsCache.update(idToUpdate, updatedCost);

        assertEquals(300.0, cachedList.get(0).getCost());
        verify(cache).put(eq("all"), eq(cachedList));
    }
}
