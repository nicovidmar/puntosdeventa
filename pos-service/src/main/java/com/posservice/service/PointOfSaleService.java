package com.posservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.posservice.dto.PointOfSaleRequest;
import com.posservice.entity.PointOfSale;

import java.util.*;

@Service
public class PointOfSaleService {

    private final RedisTemplate<String, PointOfSale> redisTemplate;
    private final String HASH_KEY = "POS";

    public PointOfSaleService(RedisTemplate<String, PointOfSale> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<PointOfSale> findAll() {
        List<Object> values = redisTemplate.opsForHash().values(HASH_KEY);
        return values.stream()
                .filter(PointOfSale.class::isInstance)
                .map(PointOfSale.class::cast)
                .toList();
    }

    public PointOfSale save(PointOfSale pos) {
        if (nameExists(pos)) {
            throw new IllegalArgumentException("Ya existe un PointOfSale con el nombre " + pos.getName());
        }
        Long newId = redisTemplate.opsForValue().increment("pos:id:seq");
        pos.setId(newId.intValue());

        redisTemplate.opsForHash().put(HASH_KEY, String.valueOf(pos.getId()), pos);
        return pos;
    }

    public PointOfSale update(int id, PointOfSaleRequest request) {
        PointOfSale existing = findById(id);

        // Validar nombre duplicado excepto para este mismo ID
        boolean nameExists = redisTemplate.opsForHash().values(HASH_KEY).stream()
                .filter(PointOfSale.class::isInstance)
                .map(PointOfSale.class::cast)
                .anyMatch(pos -> pos.getName().equalsIgnoreCase(request.getName()) && pos.getId() != id);

        if (nameExists) {
            throw new IllegalArgumentException("Ya existe un PointOfSale con el nombre " + request.getName());
        }

        existing.setName(request.getName());
        redisTemplate.opsForHash().put(HASH_KEY, String.valueOf(id), existing);
        return existing;
    }

    public void delete(int id) {
        findById(id);

        redisTemplate.opsForHash().delete(HASH_KEY, String.valueOf(id));
    }

    public PointOfSale findById(int id) {
        Object pos = redisTemplate.opsForHash().get(HASH_KEY, String.valueOf(id));
        if (pos == null) {
            throw new IllegalArgumentException("No existe PointOfSale con ID " + id);
        }
        return (PointOfSale) pos;
    }
    
    

    private boolean nameExists(PointOfSale pos) {
        // Validar que no exista un POS con el mismo nombre
        return redisTemplate.opsForHash().values(HASH_KEY).stream()
                .filter(PointOfSale.class::isInstance)
                .map(PointOfSale.class::cast)
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(pos.getName()));
    }
}
