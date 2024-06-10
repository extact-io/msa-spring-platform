package io.extact.msa.spring.platform.fw.auth.header;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;
import lombok.ToString;

@ToString
public class RmsHeaderAuthenticationToken extends AbstractAuthenticationToken implements RmsAuthentication {

    private UserIdPrincipal principal;
    private HeaderCredential credentials;
    private LoginUser loginUser;

    public RmsHeaderAuthenticationToken(UserIdPrincipal principal, HeaderCredential credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    public RmsHeaderAuthenticationToken(UserIdPrincipal principal, HeaderCredential credentials,
            Collection<? extends GrantedAuthority> authorities, LoginUser loginUser) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.loginUser = loginUser;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
    }

    public HeaderCredential getHeaderCredential() {
        return credentials;
    }

    public UserIdPrincipal getUserIdPrincipal() {
        return principal;
    }
}
