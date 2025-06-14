package io.extact.msa.spring.platform.core.auth;

@FunctionalInterface
public interface LoginUserCreator {

    /** decorateは行わずそのままLoginUserをそのまま返す */
    public static final LoginUserCreator DEFAULT_CREATOR = platformLoginUser -> platformLoginUser;

    LoginUser crete(LoginUser platformLoginUser);
}
