package io.extact.msa.spring.platform.fw.feature.auth;

import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.*;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.test.system.CapturedOutput;

import io.extact.msa.spring.platform.core.auth.user.AuthUserId;
import io.extact.msa.spring.platform.core.auth.user.LoginUserAttributesProvider;
import io.extact.msa.spring.test.spring.RestorableLoggingSuppressor;

class AbstractCacheIntegrationTest {

    static final String HIT_CACHE_MESSAGE = "Cache entry for key 'AuthUserId(userId=%s)' found";
    static final String MISS_CACHE_MESSAGE = "No cache entry for key 'AuthUserId(userId=%s)'";
    static final String CREATE_CACHE_MESSAGE = "Creating cache entry for key 'AuthUserId(userId=%s)'";

    static abstract class DefaultSettingsCase {

        @Autowired
        private LoginUserAttributesProvider<RmsLoginUserAttributes> provider;
        private RestorableLoggingSuppressor loggingSilencer;

        // インスタンスメソッドにするためにTestInstance.Lifecycle.PER_CLASSにしている
        @BeforeAll
        void beforeAll(@Autowired LoggingSystem loggingSystem) {
            loggingSilencer = new RestorableLoggingSuppressor(loggingSystem);
            loggingSilencer.suppressAllExcluding(
                    "org.springframework.cache.interceptor",
                    LogLevel.TRACE,
                    LogLevel.ERROR);
        }

        @AfterAll
        void afterAll() {
            loggingSilencer.restoreLogLevel();
        }

        @Test
        void testCachMissAndCreateCache(CapturedOutput output) {

            // given
            AuthUserId userId = new AuthUserId(1);

            // when
            RmsLoginUserAttributes first = provider.provide(userId); // キャッシュなし
            RmsLoginUserAttributes second = provider.provide(userId); // キャッシュあり

            // then
            RmsLoginUserAttributes expected = new RmsLoginUserAttributes(
                    userId,
                    "ID-1の拡張属性1",
                    "ID-1の拡張属性2",
                    "ID-1の拡張属性3");
            assertThat(first).isEqualTo(expected);

            assertThat(output.getOut()).containsSubsequence(
                    MISS_CACHE_MESSAGE.formatted(userId.value()), // キャッシュミス
                    CREATE_CACHE_MESSAGE.formatted(userId.value()), // キャッシュ生成
                    HIT_CACHE_MESSAGE.formatted(userId.value()) // 生成したキャッシュヒット
            );
        }

        @Test
        void testMasterDataNotExistsAndCreateNullCache() {
            // given
            AuthUserId userId = new AuthUserId(4);
            // when
            RmsLoginUserAttributes attributes = provider.provide(userId);
            // then
            assertThat(attributes).isNull();
        }
    }

    static abstract class ShortTimeoutSettingsCase {

        @Autowired
        private LoginUserAttributesProvider<RmsLoginUserAttributes> provider;
        private RestorableLoggingSuppressor loggingSilencer;

        @BeforeAll
        void beforeAll(@Autowired LoggingSystem loggingSystem) {
            // OutputCaptureExtensionのBeforeAllの開始は起動直後くらいでDEBUGの場合、大量にログが出ちゃってるので
            // OutputCaptureExtensionオーバーライドして、BeforeAfterAllBypassOutputCaptureExtensionを作ってそれぞ
            // れのフェーズを飛ばすようにしよう
            loggingSilencer = new RestorableLoggingSuppressor(loggingSystem);
            loggingSilencer.suppressAllExcluding("org.springframework.cache.interceptor", LogLevel.TRACE,
                    LogLevel.ERROR);
        }

        @AfterAll
        void afterAll() {
            loggingSilencer.restoreLogLevel();
        }

        @Test
        void testCacheExpiredAndCreateCache(CapturedOutput output) {

            // given(キャッシュ生成)
            AuthUserId userId = new AuthUserId(3);
            RmsLoginUserAttributes first = provider.provide(userId);
            RmsLoginUserAttributes second = provider.provide(userId);

            // when(1秒後に再度アクセス)
            Awaitility.await()
                    .pollDelay(1100, MILLISECONDS)
                    .until(() -> true);
            RmsLoginUserAttributes third = provider.provide(userId);

            // then
            // @formatter:off
            assertThat(third).isEqualTo(first).isEqualTo(second);
            assertThat(output.getOut()).containsSubsequence(
                    MISS_CACHE_MESSAGE.formatted(userId.value()),   // キャッシュミス(初回アクセスでキャッシュなし)
                    CREATE_CACHE_MESSAGE.formatted(userId.value()), // キャッシュ生成
                    HIT_CACHE_MESSAGE.formatted(userId.value()),    // キャッシュヒット(再度アクセス)
                                                                      // 1秒経過後
                    MISS_CACHE_MESSAGE.formatted(userId.value()),   // キャッシュミス(有効期間切れで破棄)
                    CREATE_CACHE_MESSAGE.formatted(userId.value())  // キャッシュ再生成
            );
            // @formatter:on
        }
    }
}
