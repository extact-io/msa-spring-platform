package io.extact.msa.spring.platform.fw.auth.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;

public class RmsJwtAuthenticationToken extends JwtAuthenticationToken implements RmsAuthentication {

    private LoginUser loginUser;

    public RmsJwtAuthenticationToken(Jwt jwt, LoginUser loginUser) {
        super(jwt);
        this.loginUser = loginUser;
    }

    public RmsJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, LoginUser loginUser) {
        super(jwt, authorities);
        this.loginUser = loginUser;
    }

    public RmsJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name,
            LoginUser loginUser) {
        super(jwt, authorities, name);
        this.loginUser = loginUser;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
    }
}
