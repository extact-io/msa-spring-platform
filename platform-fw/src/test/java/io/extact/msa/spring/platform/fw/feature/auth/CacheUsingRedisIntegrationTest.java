package io.extact.msa.spring.platform.fw.feature.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.redis.testcontainers.RedisContainer;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
class CacheUsingRedisIntegrationTest {

    @Autowired
    private RdbAttributesProvider provider;

    @Configuration(proxyBeanMethods = false)
    @Import(RdbAttributesProviderConfig.class)
    @EnableAutoConfigurationWithoutJpa
    static class TestConfig {
        @Bean
        @ServiceConnection
        @SuppressWarnings("resource")
        @ConditionalOnProperty(name = "rms.login-user-attributes.cache.type", havingValue = "redis")
        RedisContainer redisContainer() {
            return new RedisContainer("redis:6.2.6").withReuse(false);
        }
    }

    @Test
    void testExistsAttributes() {

        // given
        AuthUserId userId = new AuthUserId(2);

        // when
        RmsLoginUserAttributes attributes = provider.provide(userId);

        // then
        RmsLoginUserAttributes expected = new RmsLoginUserAttributes(
                userId,
                "ID-2の拡張属性1",
                "ID-2の拡張属性2",
                "ID-2の拡張属性3");
        assertThat(attributes).isEqualTo(expected);
    }

    @Test
    void testNotExistsAttributes() {

        // given
        AuthUserId userId = new AuthUserId(4);

        // when
        RmsLoginUserAttributes attributes = provider.provide(userId);

        // then
        assertThat(attributes).isNull();
    }
}
