package io.extact.msa.spring.platform.fw.auth.header;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.fw.auth.client.RmsClientAuthenticationToken;

public class LoginUserHeaderRequestInitializer implements ClientHttpRequestInitializer {

    @Override
    public void initialize(ClientHttpRequest request) {

        RmsClientAuthenticationToken auth = (RmsClientAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            request.getHeaders().add("rms-userId", String.valueOf(auth.getLoginUser().getUserId()));
            request.getHeaders().add("rms-roles", auth.getLoginUser().getGroupsByStringValue());
        }
    }
}
