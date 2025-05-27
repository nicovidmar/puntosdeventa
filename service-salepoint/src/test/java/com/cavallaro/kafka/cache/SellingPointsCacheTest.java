package com.cavallaro.kafka.cache;


import com.cavallaro.kafka.dto.SellingPointDTO;
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

class SellingPointsCacheTest {

    private static final String CACHE_NAME = "sellingPoints";

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private SellingPointsCache sellingPointsCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    }

    @Test
    void shouldReturnCachedSellingPoints() {
        List<SellingPointDTO> cachedList = new ArrayList<>();
        cachedList.add(new SellingPointDTO(1, "Point A"));

        when(cache.get("all", List.class)).thenReturn(cachedList);

        List<SellingPointDTO> result = sellingPointsCache.getAllCached();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Point A", result.get(0).getName());
    }

    @Test
    void shouldSaveSellingPointInCache() {
        List<SellingPointDTO> cachedList = new ArrayList<>();
        when(cache.get("all", List.class)).thenReturn(cachedList);

        SellingPointDTO newPoint = new SellingPointDTO(2, "Point B");
        sellingPointsCache.save(newPoint);

        assertTrue(cachedList.contains(newPoint));
        verify(cache).put(eq("all"), eq(cachedList));
    }

    @Test
    void shouldRemoveSellingPointFromCache() {
        Integer idToRemove = 1;
        List<SellingPointDTO> cachedList = new ArrayList<>();
        cachedList.add(new SellingPointDTO(1, "Point A"));
        cachedList.add(new SellingPointDTO(2, "Point B"));

        when(cache.get("all", List.class)).thenReturn(cachedList);

        sellingPointsCache.remove(idToRemove);

        assertEquals(1, cachedList.size());
        assertFalse(cachedList.stream().anyMatch(sp -> Objects.equals(sp.getId(), idToRemove)));
        verify(cache).put(eq("all"), eq(cachedList));
    }

    @Test
    void shouldUpdateSellingPointInCache() {
        Integer idToUpdate = 1;
        List<SellingPointDTO> cachedList = new ArrayList<>();
        cachedList.add(new SellingPointDTO(1, "Point A"));
        cachedList.add(new SellingPointDTO(2, "Point B"));

        when(cache.get("all", List.class)).thenReturn(cachedList);

        SellingPointDTO updatedPoint = new SellingPointDTO(1, "Updated Point A");
        sellingPointsCache.update(idToUpdate, updatedPoint);

        assertEquals("Updated Point A", cachedList.get(0).getName());
        verify(cache).put(eq("all"), eq(cachedList));
    }
}
