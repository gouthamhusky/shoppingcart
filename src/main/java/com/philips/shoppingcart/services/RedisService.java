package com.philips.shoppingcart.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(String key, Map<String, String> data) {
        hashOperations.putAll(key, data);
    }

    public String findInHash(String key, String field) {
        return hashOperations.get(key, field);
    }

    public void deleteField(String key, String field) {
        hashOperations.delete(key, field);
    }

}