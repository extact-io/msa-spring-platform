package io.extact.msa.spring.platform.core.jwt.provider.validate;

import java.security.interfaces.RSAPublicKey;

import org.springframework.core.io.ClassPathResource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;

import io.extact.msa.spring.platform.core.jwt.provider.JsonWebToken;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties;

public class Auth0TokenValidator implements JsonWebTokenValidator {

    private JwtProviderProperties properties;

    public Auth0TokenValidator(JwtProviderProperties properties) {
        this.properties = properties;
    }

    @Override
    public JsonWebToken validate(String token) throws JwtValidateException {

        Algorithm alg = Algorithm.RSA256(createPublicKey());

        JWTVerifier verifier = JWT.require(alg)
                .acceptExpiresAt(30)        // 有効期限の時間ズレ許容秒数
                .withClaimPresence("sub")   // サブジェクトは必須
                .withClaimPresence("jti")   // JwtIdは必須
                .withIssuer(properties.getClaim().getIssuer())  // 発行者は自分自身であること
                .build();

        return new Auth0JsonWebToken(verifier.verify(token));
    }

    private RSAPublicKey createPublicKey() {
        SecretKeyFile keyFile = new SecretKeyFile(new ClassPathResource(TEST_PUBLIC_KEY_PATH));
        return keyFile.generateKey(SecretKeyFile.PUBLIC);
    }
}
