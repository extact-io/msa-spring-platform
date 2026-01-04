package io.extact.msa.spring.platform.core.auth.configure;

import static io.extact.msa.spring.platform.core.auth.configure.AuthConfigureUtils.*;

import java.util.List;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class SimpleAuthorizeRequestConfigure implements AuthorizeRequestConfigure {

    final List<AuthorizeHttpRequestCustomizer> requestCustomizers;
    final String[] securityMatcher;

    @Override
    public AuthorizeRequestConfigure applySecurityMatcher(HttpSecurity http) {
        if (securityMatcher != null) {
            http.securityMatcher(securityMatcher);
        }
        return this;
    }

    @Override
    public HttpSecurity applyAuthorizeHttpRequests(HttpSecurity http) {
        requestCustomizers.forEach(customizer -> applyCustomizeToHttp(http, customizer));
        return http;
    }


    // ------------------------------------------------------- inner class

    public static class AuthorizeRequestConfigureBuilder {

        List<AuthorizeHttpRequestCustomizer> requestCustomizers;
        String[] securityMatcher;

        public AuthorizeRequestConfigureBuilder authorizeHttpRequest(List<AuthorizeHttpRequestCustomizer> customizers) {
            this.requestCustomizers = customizers;
            return this;
        }

        public AuthorizeRequestConfigureBuilder securityMatcher(String... patterns) {
            this.securityMatcher = patterns;
            return this;
        }

        public AuthorizeRequestConfigure build() {
            return new SimpleAuthorizeRequestConfigure(requestCustomizers, securityMatcher);
        }
    }
}