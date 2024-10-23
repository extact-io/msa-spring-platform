package io.extact.msa.spring.platform.core.auth.client;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * サーバから発行されたBearerTokenをリクエストヘッダに付加するクラス
 */
public class BearerTokenRequestInitializer implements ClientHttpRequestInitializer {

    @Override
    public void initialize(ClientHttpRequest request) {

        RmsClientAuthenticationToken auth = (RmsClientAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            request.getHeaders().setBearerAuth(auth.getBearerTokenCredential().bearToken());
        }
    }
}
