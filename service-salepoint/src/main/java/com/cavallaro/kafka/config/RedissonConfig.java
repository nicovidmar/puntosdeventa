package com.cavallaro.kafka.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedissonConfig {
    @Value("${redis.url}")
    private String redisUrl;

/*    @Value("${cache.sellingPoints.maxSize}")
    private int sellingPointsMaxSize;

    @Value("${cache.sellingPoints.expireAfterWrite}")
    private int sellingPointsExpireAfterWrite;

    @Value("${cache.sellingCosts.maxSize}")
    private int sellingCostsMaxSize;

    @Value("${cache.sellingCosts.expireAfterWrite}")
    private int sellingCostsExpireAfterWrite;*/

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
       Map<String, CacheConfig> configMap = new HashMap<>();
        // Configurar la caché para 10 minutos (600,000 ms)
        // Configurar la caché para 1 minuto (60,000 ms)
        // Configurar la caché para 30 segundos (30,000 ms)
        // Configurar la caché para 10 segundos (10,000 ms)

      //  configMap.put("sellingPoints", new CacheConfig(sellingPointsMaxSize, sellingPointsExpireAfterWrite));
      //  configMap.put("sellingCosts", new CacheConfig(sellingCostsMaxSize, sellingCostsExpireAfterWrite));
        configMap.put("sellingPoints", null);
        configMap.put("sellingCosts", null);
        return new RedissonSpringCacheManager(redissonClient, configMap);

       // return new RedissonSpringCacheManager(redissonClient);
    }
}
