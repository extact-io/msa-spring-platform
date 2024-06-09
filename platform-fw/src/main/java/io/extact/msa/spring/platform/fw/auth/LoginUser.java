package io.extact.msa.spring.platform.fw.auth;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface LoginUser {

    public static final LoginUser UNKNOWN_USER = LoginUser.of(-1, Collections.emptySet());

    int getUserId();

    Set<String> getGroups();

    default boolean isUnknownUser() {
        return this == UNKNOWN_USER;
    }

    default String getGroupsByStringValue() {
        return getGroups().stream()
                .collect(Collectors.joining(","));
    }

    static LoginUser of(int userId, Set<String> roles) {
        return new LoginUserImpl(userId, roles);
    }

    @RequiredArgsConstructor
    @Getter @ToString
    static class LoginUserImpl implements LoginUser {
        private final int userId;
        private final Set<String> groups;
    }
}