package io.extact.msa.spring.platform.fw.auth.anonymous;

import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;
import jakarta.servlet.http.HttpServletRequest;

public class RmsAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    private String key;
    private Object principal;
    private List<GrantedAuthority> authorities;
    private LoginUser loginUser;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    RmsAnonymousAuthenticationFilter(String key, Object principal, List<GrantedAuthority> authorities, LoginUser loginUser) {
        super(key, principal, authorities);
        this.key = key;
        this.principal = principal;
        this.authorities = authorities;
        this.loginUser = loginUser;
    }

    public static RmsAnonymousAuthenticationFilterBuilder builder() {
        return new RmsAnonymousAuthenticationFilterBuilder();
    }

    @Override
    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        super.setAuthenticationDetailsSource(authenticationDetailsSource);
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken token = new RmsAnonymousAuthenticationToken(this.key, this.principal,
                this.authorities, loginUser);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }


    public static class RmsAnonymousAuthenticationFilterBuilder {

        private static final LoginUser ANONYMOUS_USER = LoginUser.ANONYMOUS_USER;
        private static final UserIdPrincipal PRINCIPAL = new UserIdPrincipal(ANONYMOUS_USER.getUserId());

        private String key;
        private List<GrantedAuthority> authorities;

        RmsAnonymousAuthenticationFilterBuilder() {
            defaultSetting();
        }

        private void defaultSetting() {
            this.key = UUID.randomUUID().toString();
            this.authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
        }

        public RmsAnonymousAuthenticationFilterBuilder key(String key) {
            this.key = key;
            return this;
        }

        public RmsAnonymousAuthenticationFilterBuilder authorities(String...  authorities) {
            this.authorities = AuthorityUtils.createAuthorityList(authorities);
            return this;
        }

        public RmsAnonymousAuthenticationFilter build() {
            return new RmsAnonymousAuthenticationFilter(key, PRINCIPAL, authorities, ANONYMOUS_USER);
        }
    }
}
