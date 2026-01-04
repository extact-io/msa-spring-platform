package io.extact.msa.spring.platform.core.auth.configure;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class AuthConfigureUtils {

    public static void applyCustomizeToHttp(HttpSecurity http, AuthorizeHttpRequestCustomizer customizer) {
        try {
            http.authorizeHttpRequests(customizer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
