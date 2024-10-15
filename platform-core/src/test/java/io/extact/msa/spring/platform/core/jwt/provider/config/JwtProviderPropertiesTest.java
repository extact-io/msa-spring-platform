package io.extact.msa.spring.platform.core.jwt.provider.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.crypto.RsaKeyConversionServicePostProcessor;

import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties.ClockProperties.Type;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class JwtProviderPropertiesTest {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(JwtProviderProperties.class)
    static class TestConfig {
        @Bean
        static BeanFactoryPostProcessor conversionServicePostProcessor() {
            return new RsaKeyConversionServicePostProcessor();
        }
    }

    @Test
    void tesFull(@Autowired JwtProviderProperties properties) {

        assertThat(properties.enable()).isTrue();
        assertThat(properties.privateKey()).isNotNull();

        assertThat(properties.clock()).isNotNull();
        assertThat(properties.clock().type()).isEqualTo(Type.SYSTEM);
        assertThat(properties.clock().fixedDatetime()).isNotNull();
        assertThat(properties.clock().clock()).isNotNull();

        assertThat(properties.claim()).isNotNull();
        assertThat(properties.claim().issuer()).isEqualTo("JsonWebTokenIntegrationTest");
        assertThat(properties.claim().exp()).isEqualTo(60);
    }
}
