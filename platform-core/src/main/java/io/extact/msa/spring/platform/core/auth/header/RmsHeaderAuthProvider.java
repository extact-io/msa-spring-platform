package io.extact.msa.spring.platform.core.auth.header;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.UserAttributes;
import io.extact.msa.spring.platform.core.auth.user.UserAttributesProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RmsHeaderAuthProvider implements AuthenticationProvider {

    private final UserAttributesProvider<UserAttributes> attributesProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RmsHeaderAuthRequest request = (RmsHeaderAuthRequest) authentication;

        String[] roles = request.getHeaderCredential().roles().transform(values -> values.split(","));
        Set<String> roleSet = Stream.of(roles).collect(Collectors.toSet());

        Collection<? extends GrantedAuthority> authorities = roleSet.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UserAttributes attributes = attributesProvider.provide(request.getAuthUserId());

        LoginUser loginUser = LoginUser.of(request.getAuthUserId(), roleSet, attributes);

        RmsHeaderAuthToken token = new RmsHeaderAuthToken(
                loginUser,
                request.getHeaderCredential(),
                authorities);

        token.setDetails(request.getDetails());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RmsHeaderAuthToken.class.isAssignableFrom(authentication);
    }
}
