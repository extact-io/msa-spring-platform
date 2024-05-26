package io.extact.msa.spring.platform.core.jwt.provider.validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.Resource;

import lombok.Cleanup;

/**
 * 鍵ファイルを読み込んで鍵インスタンスにするクラス。
 */
public class SecretKeyFile {

    public static final KeyType PRIVATE = new PrivateKeyCreator();
    public static final KeyType PUBLIC = new PublicKeyCreator();

    private Resource location;

    public SecretKeyFile(Resource location) {
        this.location = location;
    }

    public String readFile() throws IOException {

        @Cleanup
        BufferedReader buff = new BufferedReader(new InputStreamReader(location.getInputStream()));

        StringBuilder pem = new StringBuilder();
        String line;
        while ((line = buff.readLine()) != null) {
            pem.append(line);
        }

        return pem.toString();
    }

    public <T> T generateKey(KeyType keyType) {
        try {
            return keyType.of(readFile());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    // ----------------------------------------------------- KeyType def.

    interface KeyType {
        <T extends RSAKey> T of(String pem);
    }

    @SuppressWarnings("unchecked")
    static class PrivateKeyCreator implements KeyType {

        @Override
        public RSAPrivateKey of(String pem) {
            String privateKey = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");
            try {
                byte[] encoded = Base64.getDecoder().decode(privateKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
                return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static class PublicKeyCreator implements KeyType {

        @Override
        public RSAPublicKey of(String pem) {
            String publicKey = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");
            try {
                byte[] encoded = Base64.getDecoder().decode(publicKey);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return (RSAPublicKey) keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
