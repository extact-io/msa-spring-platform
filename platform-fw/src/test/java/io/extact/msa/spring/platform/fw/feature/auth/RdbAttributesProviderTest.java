package io.extact.msa.spring.platform.fw.feature.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class RdbAttributesProviderTest {

    @Autowired
    private RdbAttributesProvider provider;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    @Import(RdbAttributesProviderConfig.class)
    @TestPropertySource(properties = "rms.login-user-attributes.cache.enabled = false")
    static class TestConfig {
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
