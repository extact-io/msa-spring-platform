package io.extact.msa.spring.platform.core.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Data;

@ConfigurationProperties(prefix = "rms.jwt-provider")
@Data
public class JwtProviderProperties {

    private PrivateKey privateKey;
    private Claim claim;

    @Data
    public static class PrivateKey {
        private Resource location;
    }

    @Data
    public static class Claim {

        private static final long ISSUED_AT_NOW = -1;

        private String issuer;
        private long issuedAt = -1;
        private int exp = 60;

        public boolean isIssuedAtToNow() {
            return issuedAt == ISSUED_AT_NOW;
        }

        public int getExpirationTime() {
            return getExp();
        }
    }
}
