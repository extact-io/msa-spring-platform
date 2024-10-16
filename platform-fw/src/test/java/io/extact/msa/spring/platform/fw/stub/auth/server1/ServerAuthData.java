package io.extact.msa.spring.platform.fw.stub.auth.server1;

import java.util.Set;

import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;

public record ServerAuthData(
        String userId,
        Set<String> groups) implements UserClaims {

    @Override
    public String principalName() {
        return this.userId + "@msa-rms";
    }
}
