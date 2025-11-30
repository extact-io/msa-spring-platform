package io.extact.msa.spring.platform.core.auth;

import org.springframework.security.core.Authentication;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;

public interface RmsAuthentication extends Authentication {

    LoginUser getLoginUser();

    default boolean isAnonymous() {
        return getLoginUser() == null || getLoginUser().isAnonymousUser();
    }
}