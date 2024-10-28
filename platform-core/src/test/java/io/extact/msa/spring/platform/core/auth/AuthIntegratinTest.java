package io.extact.msa.spring.platform.core.auth;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenRequestInitializer;
import io.extact.msa.spring.platform.core.auth.header.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.core.auth.header.RmsHeaderAuth;
import io.extact.msa.spring.platform.core.auth.header.RmsHeaderAuthConfig;
import io.extact.msa.spring.platform.core.auth.jwt.RmsJwtAuth;
import io.extact.msa.spring.platform.core.auth.jwt.RmsJwtAuthConfig;
import io.extact.msa.spring.platform.core.auth.testapp.client.ClientAuthData;
import io.extact.msa.spring.platform.core.auth.testapp.client.Server1Api;
import io.extact.msa.spring.platform.core.auth.testapp.client.TestClient;
import io.extact.msa.spring.platform.core.auth.testapp.client.TestClientAdapter;
import io.extact.msa.spring.platform.core.auth.testapp.server1.Server1Assert;
import io.extact.msa.spring.platform.core.auth.testapp.server1.Server1Controller;
import io.extact.msa.spring.platform.core.auth.testapp.server1.Server2Api;
import io.extact.msa.spring.platform.core.auth.testapp.server2.Server2Assert;
import io.extact.msa.spring.platform.core.auth.testapp.server2.Server2Controller;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderConfig;
import io.extact.msa.spring.platform.core.jwt.validation.AuthorizeRequestConfigure;
import io.extact.msa.spring.test.spring.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

/**
 * スタブのServer1, Server2アプリを使ってplatform.fwクラスをテストする。
 * <pre>
 * ・テストクライアント：HTTPインターフェース(TestClient)
 *     ↓ HTTP
 * ・スタブアプリ：RestController(Server1Controller) ※JWT-Auth
 * ・スタブアプリ：HTTPインターフェース(Server2Api)
 *     ↓ HTTP
 * ・スタブアプリ：RestController(Server2Controller) ※Header-Auth
 * </pre>
 */
@TestPropertySource(properties = """
        rms.auth.multi=true
        """)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthIntegratinTest {

    @Autowired
    private TestClient testClient;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutJpa
    @EnableWebSecurity(debug = true)
    @Import({
            JwtProviderConfig.class,
            RmsJwtAuthConfig.class,
            RmsHeaderAuthConfig.class })
    static class TestConfig {

        // ---------- for Spring Security
        @Bean
        @RmsJwtAuth
        AuthorizeRequestConfigure jwtAuthorizeRequestConfigure() {
            return AuthorizeRequestConfigure.builder()
                    .securityMatcher("/server1/**")
                    .authorizeHttpRequest((
                            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                                    .requestMatchers("*/login").permitAll()
                                    .requestMatchers("*/member").hasRole("member")
                                    .requestMatchers("*/admin").hasRole("admin")
                                    .requestMatchers("*/guest").permitAll()
                                    .requestMatchers("*/guest-with-login").permitAll()
                                    .anyRequest().authenticated())
                    .build();
        }

        @Bean
        @RmsHeaderAuth
        AuthorizeRequestConfigure headerAuthorizeRequestConfigure() {
            return AuthorizeRequestConfigure.builder()
                    .securityMatcher("/server2/**")
                    .authorizeHttpRequest((
                            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                                    .requestMatchers("*/not-login").permitAll()
                                    .requestMatchers("*/member-login").hasRole("member")
                                    .requestMatchers("*/admin-login").hasRole("admin")
                                    .anyRequest().authenticated())
                    .build();
        }

        @Bean
        TestClient testClient(Environment env) {

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .requestInitializer(new BearerTokenRequestInitializer())
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            Server1Api server1Api = factory.createClient(Server1Api.class);

            return new TestClientAdapter(server1Api);
        }

        // ---------- for server1
        @Bean
        Server1Controller server1Controller(Server1Assert server1Assert, Server2Api server2Api) {
            return new Server1Controller(server1Assert, server2Api);
        }

        @Bean
        Server1Assert server1Assert() {
            return new Server1AssertTest();
        }

        // ---------- for server2
        @Bean
         Server2Controller server2Controller(Server2Assert server2Assert) {
             return new Server2Controller(server2Assert);
         }

         @Bean
         Server2Assert server2Assert() {
             return new Server2AssertTest();
         }

         @Bean
         Server2Api server2Api(Environment env) {

             RestClient restClient = RestClient.builder()
                     .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                     .requestInitializer(new LoginUserHeaderRequestInitializer())
                     .build();

             RestClientAdapter adapter = RestClientAdapter.create(restClient);
             HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
             return factory.createClient(Server2Api.class);
         }
    }


    // ---------------------------------------------- lifecycle methods

    @BeforeEach
    void beforeEach() {
        // 直前のテストの状態がThreadLocalに残っているので事前にクリア
        SecurityContextHolder.clearContext();
    }


    // ---------------------------------------------- test methods

    @Test
    void testNotLogin() {

        // yet not login
        boolean result = testClient.guestApi();
        assertThat(result).isTrue();

        assertThatThrownBy(() -> testClient.memeberApi())
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });

        assertThatThrownBy(() -> testClient.adminApi())
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    @Test
    void testMemberLogin() {

        ClientAuthData authData = testClient.authenticate("1", "member");
        assertThat(authData.userId()).isEqualTo("1");
        assertThat(authData.groups()).isEqualTo(Set.of("member"));

        boolean result = testClient.guestApiWithLogin();
        assertThat(result).isTrue();

        result = testClient.memeberApi();
        assertThat(result).isTrue();

        assertThatThrownBy(() -> testClient.adminApi())
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }


    @Test
    void testAdminLogin() {

        ClientAuthData authData = testClient.authenticate("2", "admin");
        assertThat(authData.userId()).isEqualTo("2");
        assertThat(authData.groups()).isEqualTo(Set.of("admin"));

        boolean result = testClient.guestApiWithLogin();
        assertThat(result).isTrue();

        result = testClient.adminApi();
        assertThat(result).isTrue();

        assertThatThrownBy(() -> testClient.memeberApi())
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }


    // ---------------------------------------------- inner class definitions.

    static class Server1AssertTest implements Server1Assert {

        @Override
        public void doBeforeLoginAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isTrue();
        }

        @Override
        public void doMemberApiAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isFalse();
            assertThat(auth.getLoginUser().getUserId()).isEqualTo(1);
        }

        @Override
        public void doAdminApiAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isFalse();
            assertThat(auth.getLoginUser().getUserId()).isEqualTo(2);
        }

        @Override
        public void doGuestApiAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isTrue();
        }

        @Override
        public void doGuestApiWithLoginAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isFalse();
        }

        private RmsAuthentication getRmsAuthentication() {
            return (RmsAuthentication) SecurityContextHolder
                    .getContext()
                    .getAuthentication();
        }
    }

    static class Server2AssertTest implements Server2Assert {

        @Override
        public void doNotLoginApiAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isTrue();
        }

        @Override
        public void doMemberLoginApiAssert() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isFalse();
            assertThat(auth.getLoginUser().getUserId()).isEqualTo(1);
        }

        @Override
        public void doAdminLoginApi() {
            RmsAuthentication auth = getRmsAuthentication();
            assertThat(auth.getLoginUser().isUnknownUser()).isFalse();
            assertThat(auth.getLoginUser().getUserId()).isEqualTo(2);
        }

        private RmsAuthentication getRmsAuthentication() {
            return (RmsAuthentication) SecurityContextHolder
                    .getContext()
                    .getAuthentication();
        }

    }
}
