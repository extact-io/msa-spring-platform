package io.extact.msa.spring.platform.core.jwt.provider.impl;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

import io.extact.msa.spring.platform.core.jwt.JwtProviderProperties;
import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.SecretKeyFile;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;

public class Auth0RsaJwtGenerator implements JsonWebTokenGenerator {

    private JwtProviderProperties properties;

    public  Auth0RsaJwtGenerator(JwtProviderProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateToken(UserClaims userClaims) {
        Algorithm alg = Algorithm.RSA256(createPrivateKey());
        return buildClaims(userClaims).sign(alg);
    }

    private RSAPrivateKey createPrivateKey() {
        SecretKeyFile keyFile = new SecretKeyFile(properties.getPrivateKey().getLocation());
        return keyFile.generateKey(SecretKeyFile.PRIVATE);
    }

    private Builder buildClaims(UserClaims userClaims) {
        // MicroProfile-JWTで必須とされている項目のみ設定
        return JWT.create()
                .withIssuer(properties.getClaim().getIssuer())
                .withSubject(userClaims.getUserId())
                .withExpiresAt(OffsetDateTime.now()
                        .plusMinutes(properties.getClaim().getExpirationTime())
                        .toInstant())
                .withIssuedAt(resoleveIssuedAt(properties.getClaim().getIssuedAt()))
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("upn", userClaims.getUserPrincipalName())
                .withClaim("groups", new ArrayList<>(userClaims.getGroups()));
    }

    private Instant resoleveIssuedAt(long secondsFromEpoch) {
        return properties.getClaim().isIssuedAtToNow()
                ? OffsetDateTime.now().toInstant()
                : Instant.ofEpochSecond(secondsFromEpoch);
    }
}
