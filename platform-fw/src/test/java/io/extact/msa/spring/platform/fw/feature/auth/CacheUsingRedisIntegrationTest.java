package io.extact.msa.spring.platform.fw.feature.auth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.redis.testcontainers.RedisContainer;

import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.platform.fw.feature.auth.AbstractCacheIntegrationTest.DefaultSettingsCase;
import io.extact.msa.spring.platform.fw.feature.auth.AbstractCacheIntegrationTest.ShortTimeoutSettingsCase;
import io.extact.msa.spring.test.spring.StartupLogSuppressInitializer;

class CacheUsingRedisIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import(RdbAttributesProviderConfig.class)
    @EnableAutoConfigurationWithoutJpa
    static class CacheUsingRedisIntegrationTestConfig {
        @Bean
        @ServiceConnection
        @SuppressWarnings("resource")
        RedisContainer redisContainer() {
            return new RedisContainer("redis:6.2.6").withReuse(false);
        }
    }

    @SpringBootTest(classes = CacheUsingRedisIntegrationTestConfig.class, webEnvironment = WebEnvironment.NONE)
    @ContextConfiguration(initializers = StartupLogSuppressInitializer.class)
    @TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.type=redis")
    @ExtendWith(OutputCaptureExtension.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class DefaultSettingsTest extends DefaultSettingsCase {
    }

    @SpringBootTest(classes = CacheUsingRedisIntegrationTestConfig.class, webEnvironment = WebEnvironment.NONE)
    @ContextConfiguration(initializers = StartupLogSuppressInitializer.class)
    @TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.type=redis")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.redis.time-to-idle=1s")
    @ExtendWith(OutputCaptureExtension.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class ShortSettingsTest extends ShortTimeoutSettingsCase {
    }
}
