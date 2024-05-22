package io.extact.msa.spring.platform.core.jwt;

import static io.extact.msa.spring.platform.core.jwt.JsonWebTokenGeneratorTest.ImplType.*;
import static io.extact.msa.spring.platform.core.jwt.JsonWebTokenGeneratorTest.JsonWebTokenGeneratorFactory.*;
import static io.extact.msa.spring.platform.core.jwt.JsonWebTokenGeneratorTest.JsonWebTokenValidatorFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProviderProperties;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProviderProperties.Claim;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProviderProperties.PrivateKey;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Auth0RsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.impl.Jose4jRsaJwtGenerator;
import io.extact.msa.spring.platform.core.jwt.validate.Auth0TokenValidator;
import io.extact.msa.spring.platform.core.jwt.validate.Jose4jTokenValidator;
import io.extact.msa.spring.platform.core.jwt.validate.JsonWebTokenValidator;

class JsonWebTokenGeneratorTest {

    private JwtProviderProperties properties;

    @BeforeEach
    void beforeEach() {

        PrivateKey privateKey = new PrivateKey();
        privateKey.setLocation(new ClassPathResource("/jwt.key"));

        Claim claim = new Claim();
        claim.setIssuer("testApplication");
        claim.setIssuedAt(-1L);
        claim.setExp(60);

        properties = new JwtProviderProperties();
        properties.setPrivateKey(privateKey);
        properties.setClaim(claim);
    }

    @ParameterizedTest
    @MethodSource("generatorAndValidatorFactoryProvider")
    void testGenerateTokenAndValidate(
            JsonWebTokenGeneratorFactory generatorFactoyr,
            JsonWebTokenValidatorFactory validatorFactory) throws JwtValidateException {

        // テストし易いように発行日時と有効期限を固定
        long now = System.currentTimeMillis() / 1000L;  // 秒で表した現在日時
        properties.getClaim().setIssuedAt(now);           // 発行日時を固定で設定
        properties.getClaim().setExp(0);

        // Tokenの生成
        JsonWebTokenGenerator testGenerator = generatorFactoyr.create(properties);
        UserClaims userClaims = new SimpleUserClaims();
        String token = testGenerator.generateToken(userClaims);

        // 生成したTokenをJOSE4JとAuth0とで検査
        JsonWebTokenValidator validator = validatorFactory.create(properties);
        JsonWebToken jwt = validator.validate(token);

        // 復元したJSONが元通りか確認
        assertThat(jwt.getName()).isEqualTo(userClaims.getUserPrincipalName());
        assertThat(jwt.getIssuer()).isEqualTo(properties.getClaim().getIssuer());
        assertThat(jwt.getAudience()).isNull();
        assertThat(jwt.getSubject()).isEqualTo(userClaims.getUserId());
        assertThat(jwt.getTokenID()).isNotNull();
        assertThat(jwt.getIssuedAtTime()).isEqualTo(now);
        assertThat(jwt.getExpirationTime()).isBetween(now, now + 5L); // JwtClaims内部でnowをするため+5msecまでは誤差として許容
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
