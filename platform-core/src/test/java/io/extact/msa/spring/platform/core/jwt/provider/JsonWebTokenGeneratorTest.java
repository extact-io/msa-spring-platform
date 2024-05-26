package io.extact.msa.spring.platform.core.jwt.provider;

import static io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGeneratorTest.ImplType.*;
import static io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGeneratorTest.JsonWebTokenGeneratorFactory.*;
import static io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGeneratorTest.JsonWebTokenValidatorFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties.Claim;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties.ClockProperties;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Jose4jRsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.validate.Auth0TokenValidator;
import io.extact.msa.spring.platform.core.jwt.provider.validate.Jose4jTokenValidator;
import io.extact.msa.spring.platform.core.jwt.provider.validate.JsonWebTokenValidator;
import io.extact.msa.spring.platform.core.jwt.provider.validate.JwtValidateException;
import io.extact.msa.spring.platform.core.jwt.provider.validate.SecretKeyFile;

class JsonWebTokenGeneratorTest {

    private JwtProviderProperties properties;

    @BeforeEach
    void beforeEach() {

        SecretKeyFile keyFile = new SecretKeyFile(new ClassPathResource("/jwt.key"));
        RSAPrivateKey privateKey = keyFile.generateKey(SecretKeyFile.PRIVATE);

        ClockProperties clock = new ClockProperties();
        clock.enableFixedType();
        clock.setFixedDatetime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        Claim claim = new Claim();
        claim.setIssuer("testApplication");
        claim.setExp(60);

        properties = new JwtProviderProperties();
        properties.setPrivateKey(privateKey);
        properties.setClock(clock);
        properties.setClaim(claim);
    }

    @ParameterizedTest
    @MethodSource("generatorAndValidatorFactoryProvider")
    void testGenerateTokenAndValidate(
            JsonWebTokenGeneratorFactory generatorFactoyr,
            JsonWebTokenValidatorFactory validatorFactory) throws JwtValidateException {

        // Tokenの生成
        JsonWebTokenGenerator testGenerator = generatorFactoyr.create(properties);
        UserClaims userClaims = new SimpleUserClaims();
        String token = testGenerator.generateToken(userClaims);

        // 生成したTokenをJOSE4JとAuth0とで検査
        JsonWebTokenValidator validator = validatorFactory.create(properties);
        JsonWebToken jwt = validator.validate(token);

        // 復元したJSONが元通りか確認
        Instant now = properties.getClock().getFixedInstant();
        assertThat(jwt.getName()).isEqualTo(userClaims.getUserPrincipalName());
        assertThat(jwt.getIssuer()).isEqualTo(properties.getClaim().getIssuer());
        assertThat(jwt.getAudience()).isNull();
        assertThat(jwt.getSubject()).isEqualTo(userClaims.getUserId());
        assertThat(jwt.getTokenID()).isNotNull();
        assertThat(jwt.getIssuedAtTime()).isEqualTo(now.getEpochSecond());
        long exp = properties.getClaim().getExpirationTime(now).getEpochSecond();
        assertThat(jwt.getExpirationTime()).isBetween(exp, exp + 5L); // JwtClaims内部でnowをするため+5secまでは誤差として許容
        assertThat(jwt.getGroups()).hasSize(1);
        assertThat(jwt.getGroups()).containsAll(userClaims.getGroups());
    }

    static Stream<Arguments> generatorAndValidatorFactoryProvider() {
        return Stream.of(
            arguments(JOSE4J_GENERATOR, JOSE4J_VALIDATOR),
            arguments(JOSE4J_GENERATOR, AUTH0_VALIDATOR),
            arguments(AUTH0_GENERATOR, JOSE4J_VALIDATOR),
            arguments(AUTH0_GENERATOR, AUTH0_VALIDATOR)
        );
    }

    static class SimpleUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "soramame";
        }
        @Override
        public String getUserPrincipalName() {
            return "soramame@rms.com";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("1");
        }
    }

    static class JsonWebTokenGeneratorFactory {

        static final JsonWebTokenGeneratorFactory JOSE4J_GENERATOR = new JsonWebTokenGeneratorFactory(JOSE4J);
        static final JsonWebTokenGeneratorFactory AUTH0_GENERATOR = new JsonWebTokenGeneratorFactory(AUTH0);

        ImplType implType;

        JsonWebTokenGeneratorFactory(ImplType implType) {
            this.implType = implType;
        }

        JsonWebTokenGenerator create(JwtProviderProperties properties) {
            return switch (implType) {
                case JOSE4J -> new Jose4jRsaJwtGenerator(properties);
                case AUTH0 -> new Auth0RsaJwtGenerator(properties);
            };
        }

    }

    static class JsonWebTokenValidatorFactory {

        static final JsonWebTokenValidatorFactory JOSE4J_VALIDATOR = new JsonWebTokenValidatorFactory(JOSE4J);
        static final JsonWebTokenValidatorFactory AUTH0_VALIDATOR = new JsonWebTokenValidatorFactory(AUTH0);

        ImplType implType;

        JsonWebTokenValidatorFactory(ImplType implType) {
            this.implType = implType;
        }

        JsonWebTokenValidator create(JwtProviderProperties properties) {
            return switch (implType) {
                case JOSE4J -> new Jose4jTokenValidator(properties);
                case AUTH0 -> new Auth0TokenValidator(properties);
            };
        }
    }

    enum ImplType {
        JOSE4J,
        AUTH0
    }
}
