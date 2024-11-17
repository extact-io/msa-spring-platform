package io.extact.msa.spring.platform.test.stub.auth;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.core.auth.RmsAuthentication;

public class TestAuthUtils {

    public static Authentication signin(int id, String... roles) {
        RmsAuthentication testAuth = TestRmsAuthentication.builder()
                .userId(id)
                .roles(Set.of(roles))
                .build();
        SecurityContextHolder.getContext().setAuthentication(testAuth);
        return testAuth;
    }

    public static void signout(boolean quietly) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!quietly) {
                throw new IllegalStateException("Not signed in.");
            }
            return;
        }
        SecurityContextHolder.clearContext();
    }

    public static void signoutQuietly() {
        signout(true);
    }
}
