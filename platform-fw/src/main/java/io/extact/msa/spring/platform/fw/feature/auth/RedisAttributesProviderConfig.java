package io.extact.msa.spring.platform.fw.feature.auth;

import static io.extact.msa.spring.platform.fw.feature.auth.RedisAttributesCacheKeyPrefix.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.StringUtils;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;

@Configuration(proxyBeanMethods = false)
public class RedisAttributesProviderConfig {

    @Bean
    RedisTemplate<AuthUserId, RmsLoginUserAttributes> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<AuthUserId, RmsLoginUserAttributes> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new AuthUserIdCacheKeySerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    static class AuthUserIdCacheKeySerializer implements RedisSerializer<AuthUserId> {

        private final Charset charset;
        private String prefix;

        AuthUserIdCacheKeySerializer() {
            this(StandardCharsets.UTF_8);
        }

        AuthUserIdCacheKeySerializer(Charset charset) {
            this.charset = charset;
        }

        @Value("${rms.login-user-attributes.cache-name}")
        void setCacheName(String cacheName) {
            this.prefix = cacheName + SEPARATOR;
        }

        @Override
        public byte[] serialize(AuthUserId userId) throws SerializationException {
            if (userId == null) {
                return null;
            }
            String key = prefix + String.valueOf(userId.value());
            return key.getBytes(charset);
        }

        @Override
        public AuthUserId deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null) {
                return null;
            }
            String key = new String(bytes, charset);
            if (!key.startsWith(prefix)) {
                throw new IllegalArgumentException("invalid key: " + key);
            }
            int id = Integer.parseInt(StringUtils.delete(key, prefix));
            return new AuthUserId(id);
        }
    }
}
