package io.extact.msa.spring.platform.core.jwt.provider.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties;

public class Auth0RsaJwtGenerator implements JsonWebTokenGenerator {

    private JwtProviderProperties properties;

    public  Auth0RsaJwtGenerator(JwtProviderProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateToken(UserClaims userClaims) {
        Algorithm alg = Algorithm.RSA256(properties.getPrivateKey());
        return buildClaims(userClaims).sign(alg);
    }

    private Builder buildClaims(UserClaims userClaims) {
        // MicroProfile-JWTで必須とされている項目のみ設定
        Instant now = properties.getClock().getClock().instant();
        return JWT.create()
                .withSubject(userClaims.getUserId())
                .withIssuer(properties.getClaim().getIssuer())
                .withIssuedAt(now)
                .withExpiresAt(properties.getClaim().getExpirationTime(now))
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("upn", userClaims.getUserPrincipalName())
                .withClaim("groups", new ArrayList<>(userClaims.getGroups()));
    }
}
