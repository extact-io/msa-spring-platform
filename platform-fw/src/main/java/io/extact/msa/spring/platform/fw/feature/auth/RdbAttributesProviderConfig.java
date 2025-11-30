package io.extact.msa.spring.platform.fw.feature.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.jdbc.core.JdbcTemplate;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.fw.feature.datasource.FrameworkDataSourceConfig;

@Configuration(proxyBeanMethods = false)
@Import(FrameworkDataSourceConfig.class)
public class RdbAttributesProviderConfig {

    @Bean
    RdbAttributesProvider rdbLoginUserAttributesProvider(JdbcTemplate jdbcTemplate) {
        return new RdbAttributesProvider(jdbcTemplate);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "rms.login-user-attributes.cache.enabled", havingValue = "true")
    static class WithRedisCacheConfig {

        @Value("${login-user-attributes.cache.cache-name}")
        private String cacheName;

        @Bean
        RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
            return builder -> builder.withCacheConfiguration(cacheName, buildRedisCacheConfiguration());
        }

        private RedisCacheConfiguration buildRedisCacheConfiguration() {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

            ConverterRegistry registry = (ConverterRegistry) config.getConversionService();
            registry.addConverter(new CacheKeyConverter());

            return config
                    .computePrefixWith(cacheName -> cacheName + ":")
                    .withConversionService((ConversionService) registry)
                    .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                    .entryTtl(Duration.ofMinutes(10));
        }
    }

    static class CacheKeyConverter implements Converter<AuthUserId, String> {
        @Override
        public String convert(AuthUserId source) {
            return String.valueOf(source.value());
        }

    }
}
