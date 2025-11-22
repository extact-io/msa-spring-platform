package io.extact.msa.spring.platform.core.auth.anonymous;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.extact.msa.spring.platform.core.auth.user.LoginUser;
import io.extact.msa.spring.platform.core.auth.user.LoginUserCreator;
import io.extact.msa.spring.platform.core.auth.user.RmsAuthentication;
import lombok.ToString;

@ToString(callSuper = true)
public class RmsAnonymousAuthenticationToken extends AnonymousAuthenticationToken implements RmsAuthentication {

    public RmsAnonymousAuthenticationToken(
            String key,
            Object principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(key, principal, authorities);
    }

    @Override
    public LoginUser getLoginUser() {
        return (LoginUser) getPrincipal();
    }

    public static RmsAnonymousAuthenticationTokenBuilder builder() {
        return new RmsAnonymousAuthenticationTokenBuilder();
    }

    public static class RmsAnonymousAuthenticationTokenBuilder {

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
            LoginUser loginUser = creator.create(LoginUser.ANONYMOUS_USER);
            return new RmsAnonymousAuthenticationToken(key, loginUser, authorities);
        }
    }
}
