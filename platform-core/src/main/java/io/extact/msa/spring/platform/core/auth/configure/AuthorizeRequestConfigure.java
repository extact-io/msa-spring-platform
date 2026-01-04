package io.extact.msa.spring.platform.core.auth.configure;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import io.extact.msa.spring.platform.core.auth.configure.SimpleAuthorizeRequestConfigure.AuthorizeRequestConfigureBuilder;


public interface AuthorizeRequestConfigure {

    default AuthorizeRequestConfigure applySecurityMatcher(HttpSecurity http) {
        return this;
    }

    HttpSecurity applyAuthorizeHttpRequests(HttpSecurity http);

    static AuthorizeRequestConfigureBuilder builder() {
        return new AuthorizeRequestConfigureBuilder();
    }
}
