package io.extact.msa.spring.platform.fw.auth.client;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;
import io.extact.msa.spring.platform.fw.auth.UserIdPrincipal;
import lombok.ToString;

/**
 * RMSのクライアントアプリ向けAuthentication。
 * platformではConveterは提供しないため、LoginUserやGrantedAuthorityへの変換は利用者側で行うこと。<br>
 * また{@link #setDetails(Object)}に利用者側のログインユーザモデルが設定されることを想定している。
 */
@ToString
public class RmsClientAuthenticationToken extends AbstractAuthenticationToken implements RmsAuthentication {

    private UserIdPrincipal principal;
    private BearerTokenCredential credential;
    private LoginUser loginUser;

    public RmsClientAuthenticationToken(UserIdPrincipal principal, BearerTokenCredential credentials,
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
}
