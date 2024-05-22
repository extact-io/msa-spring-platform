package io.extact.msa.spring.platform.core.jwt;

import java.util.Set;

public interface JsonWebToken {

    default String getName() {
        return getClaim("upn", String.class);
    }

    default String getIssuer() {
        return getClaim("iss", String.class);
    }

    @SuppressWarnings("unchecked")
    default Set<String> getAudience() {
        return getClaim("aud", Set.class);
    }

    default String getSubject() {
        return getClaim("sub", String.class);
    }

    default String getTokenID() {
        return getClaim("jti", String.class);
    }

    default long getIssuedAtTime() {
        return getClaim("iat", Long.class);
    }

    default long getExpirationTime() {
        return getClaim("exp", Long.class);
    }

    @SuppressWarnings("unchecked")
    default Set<String> getGroups() {
        return getClaim("groups", Set.class);
    }

    Set<String> getClaimNames();

    <T> T getClaim(String claimName, Class<T> type);

    <T> T getClaim(String claimName);



}
