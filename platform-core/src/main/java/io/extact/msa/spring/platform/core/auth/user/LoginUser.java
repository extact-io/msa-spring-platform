package io.extact.msa.spring.platform.core.auth.user;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticatedPrincipal;

public interface LoginUser extends AuthenticatedPrincipal {

    public static final UserAttributes ANONYMOUS_ATTRIBUTES = new UserAttributes() {
        @Override
        public AuthUserId authUserId() {
            return AuthUserId.ANONYMOUS_ID;
        }};
    public static final LoginUser ANONYMOUS_USER = LoginUser.of(
            AuthUserId.ANONYMOUS_ID,
            Collections.emptySet(),
            ANONYMOUS_ATTRIBUTES);

    AuthUserId getUserId();

    Set<String> getGroups();

    <T extends UserAttributes> T getAttributes(Class<T> clazz);

    default boolean isUnknownUser() {
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

    static LoginUser of(AuthUserId userId, Set<String> roles, UserAttributes attributes) {
        return new SimpleLoginUser(userId, roles, attributes);
    }
}