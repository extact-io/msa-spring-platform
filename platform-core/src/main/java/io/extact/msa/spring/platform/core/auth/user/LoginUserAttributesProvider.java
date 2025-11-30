package io.extact.msa.spring.platform.core.auth.user;

public interface LoginUserAttributesProvider<T extends LoginUserAttributes> {
    T provide(AuthUserId id);
}