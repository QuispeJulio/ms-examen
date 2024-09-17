package com.codigo.ms_examen.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service

public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void guardarEnRedis(String key, String value, int exp) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, exp, TimeUnit.MINUTES);
    }

    public String getDataDesdeRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void borrarData(String key) {
        redisTemplate.delete(key);
    }

}
