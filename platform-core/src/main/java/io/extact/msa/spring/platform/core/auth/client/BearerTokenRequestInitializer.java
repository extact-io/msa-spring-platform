package io.extact.msa.spring.platform.core.auth.client;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.core.auth.RmsAuthentication;

/**
 * サーバから発行されたBearerTokenをリクエストヘッダに付加するクラス
 */
public class BearerTokenRequestInitializer implements ClientHttpRequestInitializer {

    @Override
    public void initialize(ClientHttpRequest request) {

        RmsAuthentication auth = (RmsAuthentication) SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth instanceof RmsClientAuthenticationToken clientAuth) {
            request.getHeaders().setBearerAuth(clientAuth.getBearerTokenCredential().bearToken());
        }
    }
}
