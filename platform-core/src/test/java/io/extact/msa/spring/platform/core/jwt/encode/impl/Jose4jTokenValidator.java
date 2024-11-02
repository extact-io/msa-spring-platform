package io.extact.msa.spring.platform.core.jwt.encode.impl;

import java.security.interfaces.RSAPublicKey;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.core.io.ClassPathResource;

import io.extact.msa.spring.platform.core.jwt.encode.JsonWebToken;
import io.extact.msa.spring.platform.core.jwt.encode.config.JwtEncodeProperties;

public class Jose4jTokenValidator implements JsonWebTokenValidator {

    private JwtEncodeProperties properties;

    public Jose4jTokenValidator(JwtEncodeProperties properties) {
        this.properties = properties;
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {

        JwtConsumer consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()                 // 有効期限をチェックする
                .setAllowedClockSkewInSeconds(30)           // 有効期限の時間ズレ許容秒数
                .setRequireSubject()                        // サブジェクトは必須
                .setRequireJwtId()                          // JwtIdは必須
                .setExpectedIssuer(properties.claim().issuer()) // 発行者は自分自身であること
                .setSkipDefaultAudienceValidation()         // 受信者のチェックはしない
                .setVerificationKey(createPublicKey())      // トークンの署名を検査するキー（＝署名に使ったキー）
                .setRelaxVerificationKeyValidation()        // 復号キーの形式チェックはしない
                .build();
        try {
            return new Jose4jJsonWebToken(consumer.processToClaims(token));

        } catch (InvalidJwtException e) {
            throw new JwtValidateException(e);

        }
    }

    private RSAPublicKey createPublicKey() {
        SecretKeyFile keyFile = new SecretKeyFile(new ClassPathResource(TEST_PUBLIC_KEY_PATH));
        return keyFile.generateKey(SecretKeyFile.PUBLIC);
    }
}
