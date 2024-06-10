package io.extact.msa.spring.platform.fw.auth.header;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import io.extact.msa.spring.platform.core.CoreConfiguration;
import io.extact.msa.spring.platform.core.jwt.validation.AuthorizeRequestCustomizer;
import io.extact.msa.spring.platform.fw.auth.anonymous.RmsAnonymousAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableWebSecurity(debug = true)
@Import(CoreConfiguration.class)
public class RmsHeaderAuthenticationConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizeRequestCustomizer requestCustomizer,
            AnonymousAuthenticationFilter anonymousFilter) throws Exception {

        return http
                .authorizeHttpRequests(requestCustomizer)
                .with(new RmsHeaderConfigurer<>(), Customizer.withDefaults())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RmsHeaderAuthenticationEntryPoint())
                        .accessDeniedHandler(new RmsHeaderAccessDeniedHandler()))
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
    FilterRegistrationBean<AnonymousAuthenticationFilter> wrappedAnonymousAuthenticationFilter(
            AnonymousAuthenticationFilter filter) {
        FilterRegistrationBean<AnonymousAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
