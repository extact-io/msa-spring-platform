package io.extact.msa.spring.platform.core.jwt.encode.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.extact.msa.spring.platform.core.jwt.encode.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.encode.JwtEncodeResponseAdvice;
import io.extact.msa.spring.platform.core.jwt.encode.impl.Auth0RsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.encode.impl.Jose4jRsaJwtGenerator;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtEncodeProperties.class)
@ConditionalOnEnabledJwtEncode
public class JwtEncodeConfig {

    @Bean
    @ConditionalOnProperty(prefix = "rms.jwt-encode", name = "generator", havingValue = "auth0", matchIfMissing = true)
    JsonWebTokenGenerator auth0RsaJwtGenerator(JwtEncodeProperties properties) {
        return new Auth0RsaJwtGenerator(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "rms.jwt-encode", name = "generator", havingValue = "jose4j")
    JsonWebTokenGenerator jose4jRsaJwtGenerator(JwtEncodeProperties properties) {
        return new Jose4jRsaJwtGenerator(properties);
    }

    @Bean
    JwtEncodeResponseAdvice jwtProvideResponseAdvice(JsonWebTokenGenerator generator) {
        return new JwtEncodeResponseAdvice(generator);
    }
}
