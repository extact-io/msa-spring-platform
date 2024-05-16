package io.extact.msa.spring.platform.core.jwt.validate;

import java.util.HashSet;
import java.util.Set;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.extact.msa.spring.platform.core.jwt.JsonWebToken;

public class Auth0JsonWebToken implements JsonWebToken {

    private DecodedJWT jwt;

    public Auth0JsonWebToken(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    @Override
    public String getName() {
        return jwt.getClaim("upn").asString();
    }

    @Override
    public Set<String> getGroups() {
        return new HashSet<>(jwt.getClaim("groups").asList(String.class));
    }

    @Override
    public Set<String> getClaimNames() {
        return new HashSet<>(jwt.getClaims().keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClaim(String claimName, Class<T> type) {

        if (type.equals(String.class)) {
            return (T) jwt.getClaim(claimName).asString();
        }
        if (type.equals(Long.class)) {
            return (T) jwt.getClaim(claimName).asLong();
        }
        if (type.equals(Set.class)) {
            Claim claimValue = jwt.getClaim(claimName);
            return !claimValue.isMissing() && !claimValue.isNull()
                    ? (T) new HashSet<>(jwt.getClaim(claimName).asList(String.class))
                    : null;
        }

        return (T) jwt.getClaim(claimName).toString();
    }

    @Override
    public <T> T getClaim(String claimName) {
        throw new UnsupportedOperationException();
    }
}
