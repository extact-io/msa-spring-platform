package io.extact.msa.spring.platform.fw.stub.auth.testclient;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import io.extact.msa.spring.platform.fw.auth.client.BearerTokenExtractor;
import io.extact.msa.spring.platform.fw.auth.client.RmsClientAuthenticationToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestClientAdapter implements TestClient {

    private final Server1Api server1Api;

    @Override
    public ClientAuthData authenticate(String loginId, String password) {

        ResponseEntity<ClientAuthData> response = server1Api.authenticate(loginId, password);

        ClientAuthData authData = response.getBody();
        String bearerToken = BearerTokenExtractor.extract(response.getHeaders());

        RmsClientAuthenticationToken token = RmsClientAuthenticationToken.builder()
                .userId(authData.userId())
                .bearerToken(bearerToken)
                .groups(authData.groups())
                .build();

        SecurityContextHolder.setContext(new SecurityContextImpl(token));

        return authData;
    }

    @Override
    public boolean memeberApi() {
        return server1Api.memeberApi();
    }

    @Override
    public boolean adminApi() {
        return server1Api.adminApi();
    }

    @Override
    public boolean guestApi() {
        return server1Api.guestApi();
    }

    @Override
    public boolean guestApiWithLogin() {
        return server1Api.guestApiWithLogin();
    }
}
