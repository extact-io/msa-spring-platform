package io.extact.msa.spring.platform.fw.feature.auth;

import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.util.StringUtils;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;

public class LoginUserAttributesCacheKeys {

    static final String CACHE_NAME = "loginUserAttributes";
    static final String KEY_SEPARATOR = ":";
    static final String KEY_PREFIX = CACHE_NAME + KEY_SEPARATOR;
    static final CacheKeyPrefix CACHE_KEY_PREFIX = cacheName -> cacheName + KEY_SEPARATOR;

    public static String keyOf(AuthUserId userId) {
        return KEY_PREFIX + userId.value();
    }

    public static AuthUserId toUserId(String key) {
        if (!key.startsWith(KEY_PREFIX)) {
            throw new IllegalArgumentException("invalid key: " + key);
        }
        int id = Integer.parseInt(StringUtils.delete(key, KEY_PREFIX));
        return new AuthUserId(id);
    }
}
