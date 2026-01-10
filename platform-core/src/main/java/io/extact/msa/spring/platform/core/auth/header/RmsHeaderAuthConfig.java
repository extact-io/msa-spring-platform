package io.extact.msa.spring.platform.core.auth.header;

import static io.extact.msa.spring.platform.core.auth.configure.AuthConfigureUtils.*;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.extact.msa.spring.platform.core.auth.SecurityFallbackExceptionFilter;
import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthConfig;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeRequestConfigure;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributes;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;

@Configuration(proxyBeanMethods = false)
@Import(RmsAnonymousAuthConfig.class)
public class RmsHeaderAuthConfig {

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "false", matchIfMissing = true)
    SecurityFilterChain headerAuthFilterChain1(
            HttpSecurity http,
            List<AuthorizeHttpRequestCustomizer> requestCustomizers,
            LoginUserAttributesProvider<? extends LoginUserAttributes> attributesProvider, // 利用側でBean登録すること
            AnonymousAuthenticationFilter anonymousFilter,
            ObjectMapper mapper) throws Exception {

        requestCustomizers.forEach(customizer -> applyCustomizeToHttp(http, customizer));

        return http
                .with(new RmsHeaderConfigurer<>(attributesProvider), Customizer.withDefaults())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RmsHeaderAuthEntryPoint()) // handles only AuthenticationException
                        .accessDeniedHandler(new RmsHeaderAccessDeniedHandler())) // handles only AccessDeniedException
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .addFilterBefore(new SecurityFallbackExceptionFilter(mapper), DisableEncodeUrlFilter.class)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rms.auth.multi", havingValue = "true")
    SecurityFilterChain withQualifireHeaderAuthFilterChain(
            HttpSecurity http,
            @RmsHeaderAuth AuthorizeRequestConfigure requestConfigure,
            LoginUserAttributesProvider<? extends LoginUserAttributes> attributesProvider, // 利用側でBean登録すること
            AnonymousAuthenticationFilter anonymousFilter,
            ObjectMapper mapper) throws Exception {

        return requestConfigure
                .applySecurityMatcher(http)
                .applyAuthorizeHttpRequests(http)
                .with(new RmsHeaderConfigurer<>(attributesProvider), Customizer.withDefaults())
                .anonymous(anonymous -> anonymous.authenticationFilter(anonymousFilter))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new RmsHeaderAuthEntryPoint())
                        .accessDeniedHandler(new RmsHeaderAccessDeniedHandler()))
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .requestCache(cache -> cache.disable())
                .addFilterBefore(new SecurityFallbackExceptionFilter(mapper), DisableEncodeUrlFilter.class)
                .build();
    }
}
