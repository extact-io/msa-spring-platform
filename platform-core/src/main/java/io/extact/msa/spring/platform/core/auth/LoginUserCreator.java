package io.extact.msa.spring.platform.core.auth;

@FunctionalInterface
public interface LoginUserCreator {

    public static final LoginUserCreator DEFAULT_CREATOR = new LoginUserCreator() {
        @Override
        public LoginUser crete(LoginUser platformLoginUser) {
            return platformLoginUser;
        }
    };

    LoginUser crete(LoginUser platformLoginUser);
}
