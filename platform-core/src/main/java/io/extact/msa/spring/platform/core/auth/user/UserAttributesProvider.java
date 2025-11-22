package io.extact.msa.spring.platform.core.auth.user;

public interface UserAttributesProvider<T extends UserAttributes> {
    T provide(AuthUserId id);
}