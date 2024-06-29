package com.ssuk.global.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public String getData(String key) {
        return this.stringRedisTemplate.opsForValue().get(key);
    }

    public void setData(String key, String value, long expiredTime) {
        this.stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(expiredTime));
    }

    public void deleteData(String key) {
        this.stringRedisTemplate.delete(key);
    }

    public void deleteAllData() {
        Objects.requireNonNull(this.stringRedisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(this.stringRedisTemplate.hasKey(key));
    }
}
