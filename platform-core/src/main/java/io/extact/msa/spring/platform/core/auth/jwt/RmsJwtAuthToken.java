package io.extact.msa.spring.platform.core.auth.jwt;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import io.extact.msa.spring.platform.core.auth.RmsAuthentication;
import io.extact.msa.spring.platform.core.auth.user.LoginUser;

public class RmsJwtAuthToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> implements RmsAuthentication {

    public RmsJwtAuthToken(
            Jwt token,
            LoginUser principal,
            Collection<? extends GrantedAuthority> authorities) {

        super(token, principal, token, authorities);
        this.setAuthenticated(true);
    }

    @Override
    public LoginUser getLoginUser() {
        return (LoginUser) getPrincipal();
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }
}
