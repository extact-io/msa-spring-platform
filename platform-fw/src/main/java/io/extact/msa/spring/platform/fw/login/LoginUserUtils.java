package io.extact.msa.spring.platform.fw.login;

import io.extact.msa.spring.platform.fw.auth.LoginUser;

public class LoginUserUtils {

    // TODO ThreadLocalはやめてる
    private static final InheritableThreadLocal<LoginUser> LOGIN_USER = new InheritableThreadLocal<LoginUser>() {
        @Override
        protected LoginUser initialValue() {
            return LoginUser.UNKNOWN_USER;
        }
    };

    // only login package can be used
    static void set(LoginUser loginUser) {
        LOGIN_USER.set(loginUser);
    }

    public static LoginUser get() {
        return LOGIN_USER.get();
    }

    public static void remove() {
        LOGIN_USER.remove();
    }
}
