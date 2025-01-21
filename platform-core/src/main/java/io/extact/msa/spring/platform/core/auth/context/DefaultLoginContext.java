package io.extact.msa.spring.platform.core.auth.context;

import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.core.auth.LoginUser;
import io.extact.msa.spring.platform.core.auth.RmsAuthentication;

public class DefaultLoginContext implements LoginContext {

    @Override
    public boolean isAuthenticated() {
        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return true;
    }

    @Override
    public LoginUser getLoginUser() {
        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getLoginUser() : null;
    }
}
