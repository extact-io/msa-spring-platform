package io.extact.msa.spring.platform.fw.auth.header;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.extact.msa.spring.platform.fw.auth.LoginUser;

public class RmsHeaderAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        RmsHeaderAuthenticationToken request = (RmsHeaderAuthenticationToken) authentication;

        String[] roles = request.getHeaderCredential().roles().transform(values -> values.split(","));
        Set<String> roleSet = Stream.of(roles).collect(Collectors.toSet());

        Collection<? extends GrantedAuthority> authorities = roleSet.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        LoginUser loginUser = LoginUser.of(request.getUserIdPrincipal().userId(), roleSet);

        RmsHeaderAuthenticationToken token = new RmsHeaderAuthenticationToken(request.getUserIdPrincipal(),
                request.getHeaderCredential(), authorities, loginUser);
        token.setDetails(request.getDetails());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RmsHeaderAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
