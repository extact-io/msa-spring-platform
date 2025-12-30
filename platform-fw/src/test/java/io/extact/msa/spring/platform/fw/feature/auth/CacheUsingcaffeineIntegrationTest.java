package io.extact.msa.spring.platform.fw.feature.auth;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import io.extact.msa.spring.platform.fw.feature.auth.AbstractCacheIntegrationTest.DefaultSettingsCase;
import io.extact.msa.spring.platform.fw.feature.auth.AbstractCacheIntegrationTest.ShortTimeoutSettingsCase;

class CacheUsingcaffeineIntegrationTest {

    @Configuration(proxyBeanMethods = false)
    @Import(RdbAttributesProviderConfig.class)
    static class CacheUsingcaffeineIntegrationTestConfig {
    }

    @SpringBootTest(classes = CacheUsingcaffeineIntegrationTestConfig.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.type=caffeine")
    @ExtendWith(OutputCaptureExtension.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class DefaultSettingsTest extends DefaultSettingsCase {
    }

    @SpringBootTest(classes = CacheUsingcaffeineIntegrationTestConfig.class, webEnvironment = WebEnvironment.NONE)
    @TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.type=caffeine")
    @TestPropertySource(properties = "rms.login-user-attributes.cache.caffeine.spec=expireAfterAccess=1s")
    @ExtendWith(OutputCaptureExtension.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class ShortSettingsTest extends ShortTimeoutSettingsCase {
    }
}
