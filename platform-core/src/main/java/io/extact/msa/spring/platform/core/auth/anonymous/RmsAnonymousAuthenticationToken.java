package io.extact.msa.spring.platform.core.auth.anonymous;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.extact.msa.spring.platform.core.auth.LoginUser;
import io.extact.msa.spring.platform.core.auth.LoginUserCreator;
import io.extact.msa.spring.platform.core.auth.RmsAuthentication;
import io.extact.msa.spring.platform.core.auth.UserIdPrincipal;
import lombok.ToString;

@ToString(callSuper = true)
public class RmsAnonymousAuthenticationToken extends AnonymousAuthenticationToken implements RmsAuthentication {

    private LoginUser loginUser;

    public RmsAnonymousAuthenticationToken(String key, Object principal,
            Collection<? extends GrantedAuthority> authorities, LoginUser loginUser) {
        super(key, principal, authorities);
        this.loginUser = loginUser;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
    }

    public static RmsAnonymousAuthenticationTokenBuilder builder() {
        return new RmsAnonymousAuthenticationTokenBuilder();
    }

    public static class RmsAnonymousAuthenticationTokenBuilder {

        private static final LoginUser ANONYMOUS_USER = LoginUser.ANONYMOUS_USER;
        private static final UserIdPrincipal PRINCIPAL = new UserIdPrincipal(ANONYMOUS_USER.getUserId());

        private String key;
        private List<GrantedAuthority> authorities;
        private LoginUserCreator creator = LoginUserCreator.DEFAULT_CREATOR;

        private RmsAnonymousAuthenticationTokenBuilder() {
            defaultSetting();
        }

        public String key() {
            return key;
        }

        public List<GrantedAuthority> authorities() {
            return authorities;
        }

        public UserIdPrincipal principal() {
            return PRINCIPAL;
        }

        private void defaultSetting() {
            this.key = UUID.randomUUID().toString();
            this.authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
        }

        public RmsAnonymousAuthenticationTokenBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public RmsAnonymousAuthenticationTokenBuilder withAuthorities(String...  authorities) {
            this.authorities = AuthorityUtils.createAuthorityList(authorities);
            return this;
        }

        public RmsAnonymousAuthenticationTokenBuilder withCreator(LoginUserCreator creator) {
            this.creator = creator;
            return this;
        }

        public RmsAnonymousAuthenticationToken build() {
            LoginUser loginUser = creator.create(ANONYMOUS_USER);
            return new RmsAnonymousAuthenticationToken(key, PRINCIPAL, authorities, loginUser);
        }
    }
}
