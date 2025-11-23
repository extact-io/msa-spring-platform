package io.extact.msa.spring.platform.core.auth.header;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;
import lombok.ToString;

@ToString
public class RmsHeaderAuthToken extends AbstractAuthenticationToken implements RmsAuthentication {

    private LoginUser principal;
    private HeaderCredential credentials;

    public RmsHeaderAuthToken(
            LoginUser principal,
            HeaderCredential credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
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
        return (LoginUser) principal;
    }

    public HeaderCredential getHeaderCredential() {
        return credentials;
    }
}
