package io.extact.msa.spring.platform.core.jwt.validation;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import io.extact.msa.spring.platform.core.CoreConfig;

@Configuration(proxyBeanMethods = false)
@Import(CoreConfig.class)
public class JwtValidationConfig {

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(
            @Value("${rms.jwt-validator.public-key}") RSAPublicKey key,
            @Value("${rms.jwt-validator.claim.issuer}") String issuer) {

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(key).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return jwtDecoder;
    }
}
