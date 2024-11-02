package io.extact.msa.spring.platform.core.auth.header;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthConfig;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeRequestConfigure;

@Configuration(proxyBeanMethods = false)
@Import(RmsAnonymousAuthConfig.class)
public class RmsHeaderAuthConfig {

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "false", matchIfMissing = true)
    SecurityFilterChain headerAuthFilterChain1(HttpSecurity http, AuthorizeHttpRequestCustomizer requestCustomizer,
            AnonymousAuthenticationFilter anonymousFilter) throws Exception {

        return http
                .authorizeHttpRequests(requestCustomizer)
                .with(new RmsHeaderConfigurer<>(), Customizer.withDefaults())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RmsHeaderAuthEntryPoint())
                        .accessDeniedHandler(new RmsHeaderAccessDeniedHandler()))
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "true")
    SecurityFilterChain withQualifireHeaderAuthFilterChain(HttpSecurity http,
            @RmsHeaderAuth AuthorizeRequestConfigure requestConfigure,
            AnonymousAuthenticationFilter anonymousFilter) throws Exception {

        return requestConfigure
                .applySecurityMatcher(http)
                .applyAuthorizeHttpRequests(http)
                .with(new RmsHeaderConfigurer<>(), Customizer.withDefaults())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RmsHeaderAuthEntryPoint())
                        .accessDeniedHandler(new RmsHeaderAccessDeniedHandler()))
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .build();
    }
}
