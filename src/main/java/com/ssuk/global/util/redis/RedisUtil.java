package com.ssuk.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public Object getData(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, String value, long expiredTime) {
        this.redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(expiredTime));
    }

    public void deleteData(String key) {
        this.redisTemplate.delete(key);
    }

    public void deleteAllData() {
        Objects.requireNonNull(this.redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }
}
