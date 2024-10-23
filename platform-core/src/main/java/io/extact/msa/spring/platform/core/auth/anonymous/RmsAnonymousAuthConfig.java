package io.extact.msa.spring.platform.core.auth.anonymous;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
public class RmsAnonymousAuthConfig {

    @Bean
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
