package io.extact.msa.spring.platform.core.auth.user;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter @ToString
public class SimpleLoginUser implements LoginUser {

    private final AuthUserId userId;
    private final Set<String> groups;
    private final UserAttributes attributes;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UserAttributes> T getAttributes(Class<T> clazz) {
        return (T) attributes;
    }

    @Override // AuthenticatedPrincipal#getName()
    public String getName() {
        return String.valueOf(userId.value());
    }
}