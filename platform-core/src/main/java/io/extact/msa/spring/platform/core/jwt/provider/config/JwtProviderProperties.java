package io.extact.msa.spring.platform.core.jwt.provider.config;

import java.security.interfaces.RSAPrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "rms.jwt-provider")
@Data
public class JwtProviderProperties {

    private boolean enable = false;
    private RSAPrivateKey privateKey;
    private ClockProperties clock;
    private Claim claim;

    @Data
    public static class ClockProperties {

        enum Type {
            system,
            fixed
        }

        private Type type = Type.system;
        private LocalDateTime fixedDatetime;

        public void enableFixedType() {
            this.type = Type.fixed;
        }

        public Clock getClock() {
            return switch (type) {
                case system -> Clock.systemDefaultZone();
                case fixed -> Clock.fixed(getFixedInstant(), ZoneId.systemDefault());
            };
        }

        public Instant getFixedInstant() {
            return fixedDatetime.atZone(ZoneId.systemDefault()).toInstant();
        }
    }

    @Data
    public static class Claim {

        private String issuer;
        private int exp = 60;

        public Instant getExpirationTime(Instant creationTime) {
            return creationTime.plusSeconds(exp * 60);
        }
    }
}
