package com.posservice.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.posservice.entity.PointOfSale;

@Configuration
public class RedisConfig {
	// RedisTemplate para PointOfSale (objetos)
    @Bean
    public RedisTemplate<String, PointOfSale> pointOfSaleRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PointOfSale> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<PointOfSale> serializer = new Jackson2JsonRedisSerializer<>(PointOfSale.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
