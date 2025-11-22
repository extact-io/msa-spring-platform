package io.extact.msa.spring.platform.core.auth.context;

import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;

public interface LoginContext {

    default boolean isAuthenticated() {
        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return true;
    }

    default LoginUser getLoginUser() {
        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getLoginUser() : null;
    }
}
