package io.extact.msa.spring.platform.core.auth.user;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface LoginUser {

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
        return new LoginUserImpl(userId, roles, null);
    }

    static LoginUser of(AuthUserId userId, Set<String> roles, UserAttributes attributes) {
        return new LoginUserImpl(userId, roles, attributes);
    }

    @RequiredArgsConstructor
    @Getter @ToString
    static class LoginUserImpl implements LoginUser {

        private final AuthUserId userId;
        private final Set<String> groups;
        private final UserAttributes attributes;

        @Override
        @SuppressWarnings("unchecked")
        public <T extends UserAttributes> T getAttributes(Class<T> clazz) {
            return (T) attributes;
        }
    }
}