package io.extact.msa.spring.platform.core.jwt.validate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jose4j.jwt.JwtClaims;

import io.extact.msa.spring.platform.core.jwt.JsonWebToken;

public class Jose4jJsonWebToken implements JsonWebToken {

    private JwtClaims jwt;

    public Jose4jJsonWebToken(JwtClaims claims) {
        this.jwt = claims;
    }

    @Override
    public Set<String> getGroups() {
        List<String> groups = getClaim("groups");
        return new HashSet<String>(groups);
    }

    @Override
    public Set<String> getClaimNames() {
        return new HashSet<>(jwt.getClaimNames());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClaim(String claimName) {
        return (T) jwt.getClaimValue(claimName);
    }

    @Override
    public <T> T getClaim(String claimName, Class<T> type) {
        return this.getClaim(claimName);
    }
}
