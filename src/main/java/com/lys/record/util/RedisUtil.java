package com.lys.record.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 类功能描述
 *
 * @author Lys
 * @date 2025/04/18 23:47
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    /**
     * 设置Redis键值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置对象到Redis（JSON序列化）
     */
    public void setObject(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (JsonProcessingException e) {
            log.error("Redis序列化失败", e);
        }
    }

    /**
     * 从Redis获取对象（JSON反序列化）
     */
    public <T> T getObject(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Redis反序列化失败", e);
            return null;
        }
    }

    /**
     * 获取Redis键值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置Redis键值
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 判断是否存在key
     *
     * @param key key
     * @return 是否存在key
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
