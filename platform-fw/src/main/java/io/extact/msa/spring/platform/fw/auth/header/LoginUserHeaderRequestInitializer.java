package io.extact.msa.spring.platform.fw.auth.header;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.extact.msa.spring.platform.fw.auth.RmsAuthentication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginUserHeaderRequestInitializer implements ClientHttpRequestInitializer {

    @Override
    public void initialize(ClientHttpRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth instanceof RmsAuthentication rmsAuth) {
            request.getHeaders().add("rms-userId", String.valueOf(rmsAuth.getLoginUser().getUserId()));
            request.getHeaders().add("rms-roles", rmsAuth.getLoginUser().getGroupsByStringValue());
        } else {
            log.warn("unknown Authentication. auth=>{}", auth);
        }
    }
}
