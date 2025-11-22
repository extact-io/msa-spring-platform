package io.extact.msa.spring.platform.core.auth.user;

@FunctionalInterface
public interface LoginUserCreator {

    /** decorateは行わずそのままLoginUserをそのまま返す */
    public static final LoginUserCreator DEFAULT_CREATOR = platformLoginUser -> platformLoginUser;

    LoginUser create(LoginUser platformLoginUser);
}
