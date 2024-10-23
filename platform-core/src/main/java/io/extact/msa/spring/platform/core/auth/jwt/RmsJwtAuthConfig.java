package io.extact.msa.spring.platform.core.auth.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthConfig;
import io.extact.msa.spring.platform.core.jwt.validation.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.jwt.validation.AuthorizeRequestConfigure;
import io.extact.msa.spring.platform.core.jwt.validation.JwtValidationConfig;

@Configuration(proxyBeanMethods = false)
@Import({ JwtValidationConfig.class, RmsAnonymousAuthConfig.class })
public class RmsJwtAuthConfig {

    @Bean
    @ConditionalOnBean(AuthorizeHttpRequestCustomizer.class)
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizeHttpRequestCustomizer requestCustomizer,
            Converter<Jwt, AbstractAuthenticationToken> jwtConverter, AnonymousAuthenticationFilter anonymousFilter)
            throws Exception {

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
    @Order(1) // 複数のHttpSecurityインスタンスが使われる場合を想定して優先度を指定（Order未指定よる優先）
    SecurityFilterChain withQualifireJwtAuthChain(HttpSecurity http,
            @RmsJwtAuth AuthorizeRequestConfigure requestConfigure,
            Converter<Jwt, AbstractAuthenticationToken> jwtConverter, AnonymousAuthenticationFilter anonymousFilter)
            throws Exception {

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
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return RmsJwtAuthConverter.builder().build();
    }
}
