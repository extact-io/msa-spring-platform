package io.extact.msa.spring.platform.core.auth.header;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import lombok.ToString;

@ToString
public class RmsHeaderAuthRequest extends AbstractAuthenticationToken implements Authentication {

    private AuthUserId userId;
    private HeaderCredential credentials;

    public RmsHeaderAuthRequest(AuthUserId userId, HeaderCredential credentials) {
        super(null);
        this.userId = userId;
        this.credentials = credentials;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    public HeaderCredential getHeaderCredential() {
        return credentials;
    }

    public AuthUserId getAuthUserId() {
        return userId;
    }
}
