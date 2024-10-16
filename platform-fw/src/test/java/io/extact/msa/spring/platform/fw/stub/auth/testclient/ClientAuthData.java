package io.extact.msa.spring.platform.fw.stub.auth.testclient;

import java.util.Set;

import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;

public record ClientAuthData(
        String userId,
        Set<String> groups) implements UserClaims {

    public String principalName() {
        return this.userId + "@msa-rms";
    }
}
