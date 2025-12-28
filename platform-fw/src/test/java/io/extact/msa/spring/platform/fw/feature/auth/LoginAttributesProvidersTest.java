package io.extact.msa.spring.platform.fw.feature.auth;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import com.redis.testcontainers.RedisContainer;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributes;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "rms.login-user-attributes.cache.enabled = false")
class LoginAttributesProvidersTest {

    @Autowired
    private RdbAttributesProvider rdbProvider;
    @Autowired
    private RedisAttributesProvider redisProvider;

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    @Import({ RdbAttributesProviderConfig.class,
            RedisAttributesProviderConfig.class })
    @EnableAutoConfigurationWithoutJpa
    static class TestConfig {
        @Bean
        @ServiceConnection
        @SuppressWarnings("resource")
        RedisContainer redisContainer() {
            return new RedisContainer("redis:6.2.6").withReuse(false);
        }
    }

    @BeforeAll
    void initForRedis(@Autowired RedisTemplate<AuthUserId, RmsLoginUserAttributes> redisTemplate) {

        Map<AuthUserId, RmsLoginUserAttributes> initData = Map.of(
                new AuthUserId(1),
                new RmsLoginUserAttributes(new AuthUserId(1), "ID-1の拡張属性1", "ID-1の拡張属性2", "ID-1の拡張属性3"),
                new AuthUserId(2),
                new RmsLoginUserAttributes(new AuthUserId(2), "ID-2の拡張属性1", "ID-2の拡張属性2", "ID-2の拡張属性3"),
                new AuthUserId(3),
                new RmsLoginUserAttributes(new AuthUserId(3), "ID-3の拡張属性1", "ID-3の拡張属性2", "ID-3の拡張属性3"));

        initData.entrySet().forEach(
                entry -> redisTemplate.opsForValue().set(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testTargets")
    void testExistsAttributes(LoginUserAttributesProvider<LoginUserAttributes> provider) {

        // given
        AuthUserId userId = new AuthUserId(2);

        // when
        RmsLoginUserAttributes attributes = (RmsLoginUserAttributes) provider.provide(userId);

        // then
        RmsLoginUserAttributes expected = new RmsLoginUserAttributes(
                userId,
                "ID-2の拡張属性1",
                "ID-2の拡張属性2",
                "ID-2の拡張属性3");
        assertThat(attributes).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("testTargets")
    void testNotExistsAttributes(LoginUserAttributesProvider<LoginUserAttributes> provider) {

        // given
        AuthUserId userId = new AuthUserId(4);

        // when
        RmsLoginUserAttributes attributes = (RmsLoginUserAttributes) provider.provide(userId);

        // then
        assertThat(attributes).isNull();
    }

    // テスト対象のBeanを@Autowiredのフィールドで受け取りたいためにテストのライフライクルを
    // PER_CLASSにしている。テストメソッド間でフィールドの状態を変更していないのでJUnitの
    // 並列実行をしても問題はないが利用する場合は注意すること
    private Stream<LoginUserAttributesProvider<? extends LoginUserAttributes>> testTargets() {
        return Stream.of(rdbProvider, redisProvider);
    }
}
