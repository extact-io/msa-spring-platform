package io.extact.msa.spring.platform.fw.auth.anonymous;

import java.util.Collection;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;
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
}
