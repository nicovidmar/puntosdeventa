package com.posservice.redis;

import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.posservice.entity.PointOfSale;
import com.posservice.service.PointOfSaleService;

import java.util.List;

@Component
public class InitData {

    private final RedisTemplate<String, PointOfSale> redisTemplate;
    private final PointOfSaleService pointOfSaleService;
    private static final String HASH_KEY = "POS";

    public InitData(RedisTemplate<String, PointOfSale> redisTemplate, PointOfSaleService pointOfSaleService) {
        this.redisTemplate = redisTemplate;
        this.pointOfSaleService = pointOfSaleService;
    }

    @PostConstruct
    public void init() {
        // reinicia caché de POS cada vez
        redisTemplate.delete(HASH_KEY);
        redisTemplate.delete("pos:id:seq");
        initPointsOfSale();
    }

    private void initPointsOfSale() {
        List<PointOfSale> initialPoints = List.of(
            new PointOfSale("CABA"),
            new PointOfSale("GBA_1"),
            new PointOfSale("GBA_2"),
            new PointOfSale("Santa Fe"),
            new PointOfSale("Córdoba"),
            new PointOfSale("Misiones"),
            new PointOfSale("Salta"),
            new PointOfSale("Chubut"),
            new PointOfSale("Santa Cruz"),
            new PointOfSale("Catamarca")
        );

        for (PointOfSale pos : initialPoints) {
            pointOfSaleService.save(pos);
        }
    }
}

