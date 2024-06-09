package io.extact.msa.spring.platform.fw.external;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;

import io.extact.msa.spring.platform.fw.auth.LoginUser;
import io.extact.msa.spring.platform.fw.login.LoginUserUtils;

public class PropagateLoginUserRequestInitializer implements ClientHttpRequestInitializer {

    @Override
    public void initialize(ClientHttpRequest request) {

        LoginUser loginUser = LoginUserUtils.get();
        if (loginUser.isUnknownUser()) {
            return;
        }

        request.getHeaders().add("rms-userId", String.valueOf(loginUser.getUserId()));
        request.getHeaders().add("rms-roles", loginUser.getGroupsByStringValue());
    }
}
