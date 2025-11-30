package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.data.redis.cache.CacheKeyPrefix;

public class RedisAttributesCacheKeyPrefix implements CacheKeyPrefix {

    static final String SEPARATOR = ":";

    @Override
    public String compute(String cacheName) {
        return cacheName + SEPARATOR;
    }

}
