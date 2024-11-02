package io.extact.msa.spring.platform.core.auth.configure;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lombok.RequiredArgsConstructor;

public interface AuthorizeRequestConfigure {

    default AuthorizeRequestConfigure applySecurityMatcher(HttpSecurity http) {
        return this;
    }

    HttpSecurity applyAuthorizeHttpRequests(HttpSecurity http) throws Exception;

    static AuthorizeRequestConfigureBuilder builder() {
        return new AuthorizeRequestConfigureBuilder();
    }

    @RequiredArgsConstructor
    static class SimpleAuthorizeRequestConfigure implements AuthorizeRequestConfigure {

        final AuthorizeHttpRequestCustomizer requestCustomizer;
        final String[] securityMatcher;

        @Override
        public AuthorizeRequestConfigure applySecurityMatcher(HttpSecurity http) {
            if (securityMatcher != null) {
                http.securityMatcher(securityMatcher);
            }
            return this;
        }

        @Override
        public HttpSecurity applyAuthorizeHttpRequests(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(requestCustomizer);
        }
    }

    public static class AuthorizeRequestConfigureBuilder {

        AuthorizeHttpRequestCustomizer requestCustomizer;
        String[] securityMatcher;

        public AuthorizeRequestConfigureBuilder authorizeHttpRequest(AuthorizeHttpRequestCustomizer customizer) {
            this.requestCustomizer = customizer;
            return this;
        }

        public AuthorizeRequestConfigureBuilder securityMatcher(String... patterns) {
            this.securityMatcher = patterns;
            return this;
        }

        public AuthorizeRequestConfigure build() {
            return new SimpleAuthorizeRequestConfigure(requestCustomizer, securityMatcher);
        }
    }
}
