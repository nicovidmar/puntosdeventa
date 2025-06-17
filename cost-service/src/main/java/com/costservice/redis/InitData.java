package com.costservice.redis;

import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.costservice.service.CostService;


@Component
public class InitData {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final CostService costService;
    private static final String COSTS_HASH_KEY = "costs";

    public InitData(RedisTemplate<String, Integer> redisTemplate, CostService costService) {
        this.redisTemplate = redisTemplate;
        this.costService = costService;
    }

    @PostConstruct
    public void init() {
        // reinicia caché de COST cada vez
        redisTemplate.delete(COSTS_HASH_KEY);
        initCosts();
    }

    private void initCosts() {
        int[][] initialCosts = {
            {1, 2, 2}, {1, 3, 3}, {2, 3, 5},
            {2, 4, 10}, {1, 4, 11}, {4, 5, 5},
            {2, 5, 14}, {6, 7, 32}, {8, 9, 11},
            {10, 7, 5}, {3, 8, 10}, {5, 8, 30},
            {10, 5, 5}, {4, 6, 6}
        };

        for (int[] cost : initialCosts) {
            costService.save(cost[0], cost[1], cost[2]);
        }
    }
}
