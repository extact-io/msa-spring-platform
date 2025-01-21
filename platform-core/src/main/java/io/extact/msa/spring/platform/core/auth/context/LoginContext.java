package io.extact.msa.spring.platform.core.auth.context;

import io.extact.msa.spring.platform.core.auth.LoginUser;

public interface LoginContext {

    boolean isAuthenticated();
    LoginUser getLoginUser();
}
