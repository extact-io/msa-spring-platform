package io.extact.msa.spring.platform.core.auth.user;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticatedPrincipal;

public interface LoginUser extends AuthenticatedPrincipal {

    public static final LoginUser ANONYMOUS_USER = LoginUser.of(
            AuthUserId.ANONYMOUS_ID,
            Collections.emptySet(),
            null);

    AuthUserId getUserId();

    Set<String> getGroups();

    <T extends LoginUserAttributes> T getAttributes(Class<T> clazz);

    default boolean isAnonymousUser() {
        return this == ANONYMOUS_USER;
    }

    default String getGroupsByStringValue() {
        return getGroups().stream().collect(Collectors.joining(","));
    }

    default boolean isSameUserId(String userId) {
        return getUserId().value() == Integer.parseInt(userId);
    }

    static LoginUser of(AuthUserId userId, Set<String> roles) {
        return new SimpleLoginUser(userId, roles, null);
    }

    static LoginUser of(AuthUserId userId, Set<String> roles, LoginUserAttributes attributes) {
        return new SimpleLoginUser(userId, roles, attributes);
    }
}