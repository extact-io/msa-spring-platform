package io.extact.msa.spring.platform.fw.feature.auth;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.fw.feature.auth.jackson.RmsLoginUserAttributesSerializer;

@Configuration(proxyBeanMethods = false)
public class RedisAttributesProviderConfig {

    @Bean
    RedisTemplate<AuthUserId, RmsLoginUserAttributes> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<AuthUserId, RmsLoginUserAttributes> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new AuthUserIdCacheKeySerializer());
        template.setValueSerializer(new RmsLoginUserAttributesSerializer());

        return template;
    }

    @Bean
    RedisAttributesProvider redisAttributesProvider(RedisTemplate<AuthUserId, RmsLoginUserAttributes> redisTemplate) {
        return new RedisAttributesProvider(redisTemplate);
    }

    static class AuthUserIdCacheKeySerializer implements RedisSerializer<AuthUserId> {

        private final Charset charset;

        AuthUserIdCacheKeySerializer() {
            this(StandardCharsets.UTF_8);
        }

        AuthUserIdCacheKeySerializer(Charset charset) {
            this.charset = charset;
        }

        @Override
        public byte[] serialize(AuthUserId userId) throws SerializationException {
            if (userId == null) {
                return null;
            }
            return LoginUserAttributesCacheKeys.keyOf(userId).getBytes(charset);
        }

        @Override
        public AuthUserId deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null) {
                return null;
            }
            String key = new String(bytes, charset);
            return LoginUserAttributesCacheKeys.toUserId(key);
        }
    }
}
