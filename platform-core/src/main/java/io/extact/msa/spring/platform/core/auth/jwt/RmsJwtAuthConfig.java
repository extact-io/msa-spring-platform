package io.extact.msa.spring.platform.core.auth.jwt;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthConfig;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeRequestConfigure;
import io.extact.msa.spring.platform.core.auth.user.UserAttributes;
import io.extact.msa.spring.platform.core.auth.user.UserAttributesProvider;
import io.extact.msa.spring.platform.core.jwt.decode.JwtDecodeConfig;

@Configuration(proxyBeanMethods = false)
@Import({
        RmsAnonymousAuthConfig.class,
        JwtDecodeConfig.class })
public class RmsJwtAuthConfig {

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "false", matchIfMissing = true)
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthorizeHttpRequestCustomizer requestCustomizer,
            Converter<Jwt, AbstractAuthenticationToken> jwtConverter,
            AnonymousAuthenticationFilter anonymousFilter) throws Exception {

        return http
                .authorizeHttpRequests(requestCustomizer)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)))
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "true")
    @Order(1) // @RmsHeaderAuthのHttpSecurityインスタンスも同時に使われる場合を想定して優先度を指定（Order未指定よる優先）
    SecurityFilterChain withQualifireJwtAuthChain(
            HttpSecurity http,
            @RmsJwtAuth AuthorizeRequestConfigure requestConfigure,
            Converter<Jwt, AbstractAuthenticationToken> jwtConverter,
            AnonymousAuthenticationFilter anonymousFilter) throws Exception {

        return requestConfigure
                .applySecurityMatcher(http)
                .applyAuthorizeHttpRequests(http)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)))
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoder(
            @Value("${rms.jwt-deencode.public-key}") RSAPublicKey key,
            @Value("${rms.jwt-deencode.claim.issuer}") String issuer) {

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(key).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return jwtDecoder;
    }

    @Bean
    @ConditionalOnMissingBean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(
            UserAttributesProvider<? extends UserAttributes> attributesProvider) { // 利用側でBean登録する
        return RmsJwtAuthConverter.builder()
                .userAttributesProvider(attributesProvider)
                .build();
    }
}
