package io.extact.msa.spring.platform.core.auth.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.extact.msa.spring.platform.core.auth.LoginUser;
import io.extact.msa.spring.platform.core.auth.LoginUserCreator;
import io.extact.msa.spring.platform.core.auth.RmsAuthentication;
import io.extact.msa.spring.platform.core.auth.UserIdPrincipal;
import lombok.ToString;

/**
 * RMSのクライアントアプリ向けAuthentication。
 * platformではConveterは提供しないため、LoginUserやGrantedAuthorityへの変換は利用者側で行うこと。<br>
 * また{@link #setDetails(Object)}にアプリ側のログインユーザモデルが設定されることを想定している。
 */
@ToString
public class RmsClientAuthenticationToken extends AbstractAuthenticationToken implements RmsAuthentication {

    private UserIdPrincipal principal;
    private BearerTokenCredential credential;
    private LoginUser loginUser;

    RmsClientAuthenticationToken(UserIdPrincipal principal, BearerTokenCredential credentials,
            Collection<? extends GrantedAuthority> authorities, LoginUser loginUser) {
        super(authorities);
        this.principal = principal;
        this.credential = credentials;
        this.loginUser = loginUser;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credential;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
    }

    public BearerTokenCredential getBearerTokenCredential() {
        return credential;
    }

    public UserIdPrincipal getUserIdPrincipal() {
        return principal;
    }

    public static RmsClientAuthenticationTokenBuilder builder() {
        return new RmsClientAuthenticationTokenBuilder();
    }

    public static class RmsClientAuthenticationTokenBuilder {

        private String userId;
        private String bearerToken;
        private Set<String> groups;
        private LoginUserCreator creator = LoginUserCreator.DEFAULT_CREATOR;

        public RmsClientAuthenticationTokenBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public RmsClientAuthenticationTokenBuilder bearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        public RmsClientAuthenticationTokenBuilder groups(Set<String> groups) {
            this.groups = groups; // ROLE_は内部で追加される
            return this;
        }

        public RmsClientAuthenticationTokenBuilder loginUserCreator(LoginUserCreator creator) {
            this.creator = creator;
            return this;
        }

        public RmsClientAuthenticationToken build() {

            UserIdPrincipal principal = new UserIdPrincipal(userId);
            BearerTokenCredential credential = new BearerTokenCredential(bearerToken);
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(groups);

            LoginUser platformLoginUser = LoginUser.of(userId, groups);
            LoginUser loginUser = creator.create(platformLoginUser);

            return new RmsClientAuthenticationToken(principal, credential, authorities, loginUser);
        }

    }
}
