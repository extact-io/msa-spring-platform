package io.extact.msa.spring.platform.fw.auth.jwt;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;

public class RmsJwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> implements RmsAuthentication {

    private LoginUser loginUser;
    private String principalName;

    public RmsJwtAuthenticationToken(UserIdPrincipal principal, String principlaName, Jwt token,
            Collection<? extends GrantedAuthority> authorities, LoginUser loginUser) {

        super(token, principal, token, authorities);
        this.setAuthenticated(true);
        this.principalName = principlaName;
        this.loginUser = loginUser;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
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
