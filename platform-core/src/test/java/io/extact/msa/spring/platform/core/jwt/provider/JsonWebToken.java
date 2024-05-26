package io.extact.msa.spring.platform.core.jwt.provider;

import java.util.Set;

import org.springframework.security.oauth2.jwt.JwtClaimNames;

public interface JsonWebToken {

    default String getIssuer() {
        return getClaim(JwtClaimNames.ISS, String.class);
    }

    @SuppressWarnings("unchecked")
    default Set<String> getAudience() {
        return getClaim(JwtClaimNames.AUD, Set.class);
    }

    default String getSubject() {
        return getClaim(JwtClaimNames.SUB, String.class);
    }

    default String getTokenID() {
        return getClaim(JwtClaimNames.JTI, String.class);
    }

    default long getIssuedAtTime() {
        return getClaim(JwtClaimNames.IAT, Long.class);
    }

    default long getExpirationTime() {
        return getClaim(JwtClaimNames.EXP, Long.class);
    }

    default String getName() {
        return getClaim("upn", String.class);
    }

    @SuppressWarnings("unchecked")
    default Set<String> getGroups() {
        return getClaim("groups", Set.class);
    }

    Set<String> getClaimNames();

    <T> T getClaim(String claimName, Class<T> type);

    <T> T getClaim(String claimName);
}
