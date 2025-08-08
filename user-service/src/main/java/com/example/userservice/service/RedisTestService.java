package com.example.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisTestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void testConnection() {
        try {
            // Set a test key-value pair
            redisTemplate.opsForValue().set("test:key", "RedisConnectionTest");
            // Retrieve the value
            String value = redisTemplate.opsForValue().get("test:key");
            log.info(value != null ? "Redis connection successful: " + value : "Redis connection failed");
        } catch (Exception e) {
            log.error("Redis connection failed: " + e.getMessage());
        }
    }
}