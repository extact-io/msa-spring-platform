package io.extact.msa.spring.platform.core.jwt.decode;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.jwt.encode.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.encode.JwtEncodeConfig;
import io.extact.msa.spring.platform.core.jwt.encode.UserClaims;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;
import io.extact.msa.spring.test.spring.NopResponseErrorHandler;

public class JwtDecodeTest {

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @Nested
    class ValidTokenTest {

        @Autowired
        private TestClient testClient;

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = testClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(actual.getBody()).isEqualTo("ok");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-deencode.public-key=classpath:/jwt.pub-error.key")
    @Nested
    class InvalidPublicKeyTokenTest {

        @Autowired
        private TestClient testClient;

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = testClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("Signed JWT rejected");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-encode.clock.type=fixed")
    @TestPropertySource(properties = "rms.jwt-encode.clock.fixed-datetime=2024-01-01T12:30")
    @Nested
    class InvalidExpTokenTest {

        @Autowired
        private TestClient testClient;

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = testClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("Jwt expired");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-deencode.claim.issuer=dummy")
    @Nested
    class InvalidIssuerTokenTest {

        @Autowired
        private TestClient testClient;

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = testClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("iss claim is not valid");
        }
    }


    // ----------------------------------------------------- setup fixture

    private static final UserClaims TEST_USER = new UserClaims() {

        @Override
        public String userId() {
            return "test";
        }

        @Override
        public String principalName() {
            return "test";
        }

        @Override
        public Set<String> groups() {
            return Set.of("roleA");
        }
    };

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableWebSecurity(debug = true)
    @Import({
            JwtEncodeConfig.class,
            JwtDecodeConfig.class })
    static class TestConfig {

        @Bean
        AuthorizeHttpRequestCustomizer authorizeRequestCustomizer() {
            // TODO Improve when https://github.com/microsoft/vscode-java-pack/issues/530 is fixed
            return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                    .anyRequest().authenticated();
        }

        @Bean
        TestController testController() {
            return new TestController();
        }

        @Bean
        TestClient testClient(Environment env) throws Exception {

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .defaultStatusHandler(new NopResponseErrorHandler())
                    .build();
            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

            return factory.createClient(TestClient.class);
        }
    }


    // ----------------------------------------------------- client side stub interface

    public interface TestClient {

        @GetExchange("/hello")
        ResponseEntity<String> hello(@RequestHeader Map<String, ?> headers);
    }


    // ----------------------------------------------------- server side stub classes

    @RestController
    static class TestController {

        @GetMapping("/hello")
        public String hello() {
            return "ok";
        }
    }
}
