package io.extact.msa.spring.platform.fw.auth.jwt;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import io.extact.msa.spring.platform.core.jwt.validation.AuthorizeRequestCustomizer;
import io.extact.msa.spring.platform.core.jwt.validation.JwtValidationConfiguration;
import io.extact.msa.spring.platform.fw.auth.anonymous.RmsAnonymousAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableWebSecurity(debug = true)
@Import(JwtValidationConfiguration.class)
public class RmsJwtAuthenticationConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizeRequestCustomizer requestCustomizer,
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
    @ConditionalOnMissingBean
    AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        return RmsAnonymousAuthenticationFilter.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return RmsJwtAuthenticationConverter.builder().build();
    }

    @Bean
    FilterRegistrationBean<AnonymousAuthenticationFilter> wrappedAnonymousAuthenticationFilter(
            AnonymousAuthenticationFilter filter) {
        FilterRegistrationBean<AnonymousAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
