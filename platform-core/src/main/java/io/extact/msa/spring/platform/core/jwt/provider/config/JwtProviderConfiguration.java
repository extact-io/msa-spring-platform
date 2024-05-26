package io.extact.msa.spring.platform.core.jwt.provider.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.extact.msa.spring.platform.core.CoreConfiguration;
import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProvideResponseAdvice;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Jose4jRsaJwtGenerator;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProviderProperties.class)
@ConditionalOnEnabledJwtProvider
@Import(CoreConfiguration.class)
public class JwtProviderConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "rms.jwt-provider", name = "generator", havingValue = "auth0", matchIfMissing = true)
    JsonWebTokenGenerator auth0RsaJwtGenerator(JwtProviderProperties properties) {
        return new Auth0RsaJwtGenerator(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "rms.jwt-provider", name = "generator", havingValue = "jose4j")
    JsonWebTokenGenerator jose4jRsaJwtGenerator(JwtProviderProperties properties) {
        return new Jose4jRsaJwtGenerator(properties);
    }

    @Bean
    JwtProvideResponseAdvice jwtProvideResponseAdvice(JsonWebTokenGenerator generator) {
        return new JwtProvideResponseAdvice(generator);
    }
}
