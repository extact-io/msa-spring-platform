package io.extact.msa.spring.platform.core.jwt.provider.impl;

import static org.jose4j.jws.AlgorithmIdentifiers.*;

import java.time.Instant;
import java.util.ArrayList;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;

import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties;

public class Jose4jRsaJwtGenerator implements JsonWebTokenGenerator {

    private JwtProviderProperties properties;

    public Jose4jRsaJwtGenerator(JwtProviderProperties properties) {
        this.properties = properties;
    }

    @Override
    public String generateToken(UserClaims userClaims) {

        JsonWebSignature jws = new JsonWebSignature(); // 署名オブジェクト

        JwtClaims claims = createClaims(userClaims);
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(RSA_USING_SHA256);
        jws.setKey(properties.getPrivateKey()); // RSA秘密鍵(p8フォーマット)
        jws.setDoKeyValidation(false);

        try {
            // ClaimsのJSONを秘密鍵で署名
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new IllegalStateException(e);
        }
    }

    private JwtClaims createClaims(UserClaims userClaims) {

        // MicroProfile-JWTで必須とされている項目のみ設定
        JwtClaims claims = new JwtClaims();

        // 発行者
        claims.setIssuer(properties.getClaim().getIssuer());
        // ユーザ識別子
        claims.setSubject(userClaims.getUserId());
        // 発行日時(iat)
        Instant now = properties.getClock().getClock().instant();
        claims.setIssuedAt(NumericDate.fromMilliseconds(now.toEpochMilli()));
        // 有効期限(exp)
        Instant expirationTime = properties.getClaim().getExpirationTime(now);
        claims.setExpirationTime(NumericDate.fromMilliseconds(expirationTime.toEpochMilli()));
        // tokenId(jti)
        claims.setGeneratedJwtId();
        // ユーザ名(MicroProfile-JWTのカスタムClaim)
        claims.setStringClaim("upn", userClaims.getUserPrincipalName());
        // グループ名(MicroProfile-JWTのカスタムClaim)
        claims.setStringListClaim("groups", new ArrayList<>(userClaims.getGroups()));

        return claims;
    }
}
