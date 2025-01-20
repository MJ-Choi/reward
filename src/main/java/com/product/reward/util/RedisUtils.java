package com.product.reward.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.product.reward.config.redis.RedisConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;

@Slf4j
@AllArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate template;
    private final RedisConfiguration config;
    private final Gson gson = new GsonBuilder().create();

    /**
     * 조회
     *
     * @param key 레디스 키
     * @return 값
     */
    public String getStr(String key) {
        return template.opsForValue().get(key);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        String value = getStr(key);
        try {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new ArrayList<>();
    }

    public <T> Set<T> getSet(String key, Class<T> clazz) {
        String value = getStr(key);
        try {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new HashSet<>();
    }

    public <K, V> Map<K, List<V>> getMapList(String key, Class<K> kClass, Class<V> vClass) {
        String value = getStr(key);
        try {
            Type type = TypeToken.getParameterized(Map.class,
                            kClass, TypeToken.getParameterized(List.class, vClass).getType())
                    .getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new HashMap<>();
    }

    public <K, V> LinkedHashMap<K, V> getLinkedMap(String key, Class<K> kClass, Class<V> vClass) {
        String value = getStr(key);
        if (!StringUtils.hasText(value)) {
            return new LinkedHashMap<>();
        }
        try {
            Type type = TypeToken.getParameterized(LinkedHashMap.class, kClass, vClass).getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new LinkedHashMap<>();
    }

    public <K, V> Map<K, V> getMap(String key, Class<K> kClass, Class<V> vClass) {
        String value = getStr(key);
        if (!StringUtils.hasText(value)) {
            return new HashMap<>();
        }
        try {
            Type type = TypeToken.getParameterized(Map.class, kClass, vClass).getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new HashMap<>();
    }

    public <K1, K2, V> Map<K1, Map<K2, V>> getMap(String key, Class<K1> k1Class, Class<K2> k2Class, Class<V> vClass) {
        String value = getStr(key);
        if (!StringUtils.hasText(key)) {
            return new HashMap<>();
        }
        try {
            Type type = TypeToken.getParameterized(Map.class,
                            k1Class, TypeToken.getParameterized(Map.class, k2Class, vClass).getType())
                    .getType();
            return gson.fromJson(value, type);
        } catch (Exception e) {
            log.error("failed to convert {}: ", value, e);
        }
        return new HashMap<>();
    }

    public boolean isExistKey(String key) {
        return template.hasKey(key);
    }

    /**
     * 데이터 추가/갱신
     *
     * @param key   레디스 키
     * @param value 레디스 값
     * @return 성공여부
     */
    public boolean setStr(String key, String value) {
        try {
            // redis lock을 위한 임시키
            template.opsForValue().set(key.concat("lock"), "", Duration.ofMillis(config.getTimeoutMs()));
            // 데이터 저장
            template.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("cannot set to redis: ", e);
            return false;
        }
        return true;
    }

    public <T> boolean setStr(String key, List<T> value) {
        String strValue = gson.toJson(value);
        return setStr(key, strValue);
    }

    public <T> boolean setStr(String key, Set<T> value) {
        String strValue = gson.toJson(value);
        return setStr(key, strValue);
    }

    public <K, V> boolean setStr(String key, Map<K, V> value) {
        String strValue = gson.toJson(value);
        return setStr(key, strValue);
    }

    /**
     * 연결된 레디스 데이터베이스의 모든 데이터 삭제
     *
     * @param key 레디스 키
     * @return 성공여부
     */
    public boolean remove(String key) {
        return template.delete(key);
    }

    public boolean flushdb() {
        template.execute((RedisCallback<Boolean>) conn -> {
            conn.serverCommands().flushAll();
            return true;
        });
        return false;
    }
}
