package io.extact.msa.spring.platform.fw.feature.auth;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.jdbc.core.JdbcTemplate;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.fw.feature.auth.jackson.RmsLoginUserAttributesSerializer;
import io.extact.msa.spring.platform.fw.infrastructure.datasource.FrameworkDataSourceConfig;
import lombok.Data;

@Configuration(proxyBeanMethods = false)
@Import(FrameworkDataSourceConfig.class)
public class RdbAttributesProviderConfig {

    @Bean // JdbcTemplate from FrameworkDataSourceConfig
    RdbAttributesProvider rdbLoginUserAttributesProvider(JdbcTemplate jdbcTemplate) {
        return new RdbAttributesProvider(jdbcTemplate);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "rms.login-user-attributes.cache.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "rms.login-user-attributes.cache.type", havingValue = "redis")
    @EnableConfigurationProperties
    @EnableCaching
    static class WithRedisCacheConfig {

        @Bean
        @ConfigurationProperties("rms.login-user-attributes.cache.redis")
        RedisCacheProperties redisCacheProperties() {
            return new RedisCacheProperties();
        }

        @Bean
        RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheProperties props) {
            return builder -> {
                builder = props.isEnableStatistics() ? builder.enableStatistics() : builder;
                builder.withCacheConfiguration(
                        LoginUserAttributesCacheKeys.CACHE_NAME,
                        buildRedisCacheConfiguration(props));
            };
        }

        private RedisCacheConfiguration buildRedisCacheConfiguration(RedisCacheProperties props) {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

            ConverterRegistry registry = (ConverterRegistry) config.getConversionService();
            registry.addConverter(new CacheKeyConverter());

            config = !props.isCacheNullValues() ? config.disableCachingNullValues() : config;
            return config
                    .computePrefixWith(LoginUserAttributesCacheKeys.CACHE_KEY_PREFIX)
                    .withConversionService((ConversionService) registry)
                    .serializeValuesWith(SerializationPair.fromSerializer(new RmsLoginUserAttributesSerializer()))
                    .entryTtl(props.getTimeToIdle());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "rms.login-user-attributes.cache.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "rms.login-user-attributes.cache.type", havingValue = "caffeine")
    @EnableConfigurationProperties
    @EnableCaching
    static class WithCaffeineCacheConfig {

        @Bean
        @ConfigurationProperties("rms.login-user-attributes.cache.caffeine")
        CaffeinCacheProperties caffeinCacheProperties() {
            return new CaffeinCacheProperties();
        }

        @Bean
        CaffeineCacheManager caffeinCacheManager(CaffeinCacheProperties props) {
            CaffeineCacheManager manager = new CaffeineCacheManager(LoginUserAttributesCacheKeys.CACHE_NAME);
            manager.setAllowNullValues(props.isCacheNullValues());
            manager.setCacheSpecification(props.getSpec());
            return manager;
        }
    }

    static class CacheKeyConverter implements Converter<AuthUserId, String> {
        @Override
        public String convert(AuthUserId source) {
            return String.valueOf(source.value());
        }

    }

    @Data
    static class RedisCacheProperties {
        // spring.cache.redis.*から必要な設定を選択
        private Duration timeToIdle;
        private boolean cacheNullValues;
        private boolean enableStatistics;
    }

    @Data
    static class CaffeinCacheProperties {
        private boolean cacheNullValues;
        private String spec;
    }
}
