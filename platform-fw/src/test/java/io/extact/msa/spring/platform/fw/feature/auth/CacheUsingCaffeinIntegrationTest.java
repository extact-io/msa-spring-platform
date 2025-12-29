package io.extact.msa.spring.platform.fw.feature.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import io.extact.msa.spring.test.spring.RestorableLoggingSilencer;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = "rms.login-user-attributes.cache.enabled=true")
@TestPropertySource(properties = "rms.login-user-attributes.cache.type=caffeine")
@ExtendWith(OutputCaptureExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CacheUsingCaffeinIntegrationTest {

    @Autowired
    private LoginUserAttributesProvider<RmsLoginUserAttributes> provider;
    private RestorableLoggingSilencer loggingSilencer;

    @Configuration(proxyBeanMethods = false)
    @Import(RdbAttributesProviderConfig.class)
    static class TestConfig {
    }

    @BeforeAll
    void beforeAll(@Autowired LoggingSystem loggingSystem) {
        // OutputCaptureExtensionのBeforeAllの開始は起動直後くらいでDEBUGの場合、大量にログが出ちゃってるので
        // OutputCaptureExtensionオーバーライドして、BeforeAfterAllBypassOutputCaptureExtensionを作ってそれぞ
        // れのフェーズを飛ばすようにしよう
        loggingSilencer = new RestorableLoggingSilencer(loggingSystem);
        loggingSilencer.muteLogLevel("org.springframework.cache.interceptor", LogLevel.TRACE, LogLevel.ERROR);
    }

    @AfterAll
    void afterAll() {
        loggingSilencer.restoreLogLevel();
    }

    @Test
    void testCachMissAndReturnCached(CapturedOutput output) {

        // given
        AuthUserId userId = new AuthUserId(1);

        // when
        RmsLoginUserAttributes first = provider.provide(userId);

        // キャッシュミスの確認

        RmsLoginUserAttributes second = provider.provide(userId);

        // then
        RmsLoginUserAttributes expected = new RmsLoginUserAttributes(
                userId,
                "ID-1の拡張属性1",
                "ID-1の拡張属性2",
                "ID-1の拡張属性3");
        assertThat(first).isEqualTo(expected);
        assertThat(first).isSameAs(second); // メモリ上のキャッシュなので参照が同じこと
    }

    @Test
    void testCacheHit() {

        // given
        AuthUserId userId = new AuthUserId(2);

        // when
        RmsLoginUserAttributes first = provider.provide(userId);
        RmsLoginUserAttributes second = provider.provide(userId);

        // then
        RmsLoginUserAttributes expected = new RmsLoginUserAttributes(
                userId,
                "ID-2の拡張属性1",
                "ID-2の拡張属性2",
                "ID-2の拡張属性3");
        assertThat(first).isEqualTo(expected);
        assertThat(first).isSameAs(second); // メモリ上のキャッシュなので参照が同じこと
    }

    @Test
    void testNotExistsAttributes() {

        // given
        AuthUserId userId = new AuthUserId(4);

        // when
        RmsLoginUserAttributes attributes = provider.provide(userId);

        // then
        assertThat(attributes).isNull();
    }
}
