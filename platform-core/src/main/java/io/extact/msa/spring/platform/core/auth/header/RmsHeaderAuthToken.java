package io.extact.msa.spring.platform.core.auth.header;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;
import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import lombok.ToString;

@ToString
public class RmsHeaderAuthToken extends AbstractAuthenticationToken implements RmsAuthentication {

    private AuthUserId principal;
    private HeaderCredential credentials;
    private LoginUser loginUser;

    public RmsHeaderAuthToken(AuthUserId principal, HeaderCredential credentials,
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

    public AuthUserId getUserIdPrincipal() {
        return principal;
    }
}
