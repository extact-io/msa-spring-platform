package io.extact.msa.spring.platform.core.auth.jwt;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;

public class RmsJwtAuthToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> implements RmsAuthentication {

    private String principalName;

    public RmsJwtAuthToken(
            Jwt token,
            LoginUser principal,
            Collection<? extends GrantedAuthority> authorities,
            String principlaName) {

        super(token, principal, token, authorities);
        this.setAuthenticated(true);
        this.principalName = principlaName;
    }

    @Override
    public LoginUser getLoginUser() {
        return (LoginUser) getPrincipal();
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    @Override
    public String getName() {
        return this.principalName;
    }
}
