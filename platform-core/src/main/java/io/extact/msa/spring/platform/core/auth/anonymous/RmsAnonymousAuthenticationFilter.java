package io.extact.msa.spring.platform.core.auth.anonymous;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.extact.msa.spring.platform.core.auth.anonymous.RmsAnonymousAuthenticationToken.RmsAnonymousAuthenticationTokenBuilder;
import io.extact.msa.spring.platform.core.auth.user.LoginUser;

public class RmsAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {


    private RmsAnonymousAuthenticationTokenBuilder tokenBuilder;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    RmsAnonymousAuthenticationFilter(RmsAnonymousAuthenticationTokenBuilder tokenBuilder) {
        super(tokenBuilder.key(), LoginUser.ANONYMOUS_USER, tokenBuilder.authorities());
        this.tokenBuilder = tokenBuilder;
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
        AnonymousAuthenticationToken token = tokenBuilder.build();
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }


    public static class RmsAnonymousAuthenticationFilterBuilder {

        private RmsAnonymousAuthenticationTokenBuilder tokenBuilder;

        RmsAnonymousAuthenticationFilterBuilder() {
            this.tokenBuilder = RmsAnonymousAuthenticationToken.builder();
        }

        public RmsAnonymousAuthenticationFilterBuilder key(String key) {
            tokenBuilder.withKey(key);
            return this;
        }

        public RmsAnonymousAuthenticationFilterBuilder authorities(String...  authorities) {
            tokenBuilder.withAuthorities(authorities);
            return this;
        }

        public RmsAnonymousAuthenticationFilter build() {
            return new RmsAnonymousAuthenticationFilter(tokenBuilder);
        }
    }
}
