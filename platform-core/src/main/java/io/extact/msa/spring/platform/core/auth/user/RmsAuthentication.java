package io.extact.msa.spring.platform.core.auth.user;

import org.springframework.security.core.Authentication;

public interface RmsAuthentication extends Authentication {
    LoginUser getLoginUser();
}