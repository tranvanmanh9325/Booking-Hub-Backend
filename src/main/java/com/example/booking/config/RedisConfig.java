package com.example.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

@Configuration
public class RedisConfig {

    @Bean
    public org.springframework.cache.CacheManager cacheManager(@NonNull RedisConnectionFactory connectionFactory) {
        return org.springframework.data.redis.cache.RedisCacheManager.builder(connectionFactory).build();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(@NonNull RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}