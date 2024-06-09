package io.extact.msa.spring.platform.fw.auth.anonymous;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

public class RmsAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    private String key;
    private Object principal;
    private List<GrantedAuthority> authorities;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();


    public RmsAnonymousAuthenticationFilter(String key, Object principal, List<GrantedAuthority> authorities) {
        super(key, principal, authorities);
        this.key = key;
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken token = new RmsAnonymousAuthenticationToken(this.key, this.principal,
                this.authorities, LoginUser.UNKNOWN_USER);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }

    @Override
    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        super.setAuthenticationDetailsSource(authenticationDetailsSource);
        this.authenticationDetailsSource = authenticationDetailsSource;
    }
}
