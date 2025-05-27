package com.cavallaro.kafka.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedissonConfig {
    @Value("${redis.url}")
    private String redisUrl;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }


  @Bean
  public CacheManager cacheManager(RedissonClient redissonClient) {
      return new RedissonSpringCacheManager(redissonClient);
  }

}
