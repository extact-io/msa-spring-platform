package io.extact.msa.spring.platform.core.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderConfiguration;
import io.extact.msa.spring.platform.core.jwt.validator.AuthorizeRequestCustomizer;
import io.extact.msa.spring.platform.core.jwt.validator.JwtValidatorConfiguration;
import io.extact.msa.spring.platform.core.testlib.NopResponseErrorHandler;

public class JsonWebTokenValidationTest {

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @Nested
    class ValidTokenTest {

        private TestRestClient resourceClient;

        @BeforeEach
        void beforeEach(@Value("${local.server.port}") int port) throws Exception {
            this.resourceClient = JsonWebTokenValidationTest.this.createRestClient(port);
        }

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = resourceClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(actual.getBody()).isEqualTo("ok");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-validator.public-key=classpath:/jwt.pub-error.key")
    @Nested
    class InvalidPublicKeyTokenTest {

        private TestRestClient resourceClient;

        @BeforeEach
        void beforeEach(@Value("${local.server.port}") int port) throws Exception {
            this.resourceClient = JsonWebTokenValidationTest.this.createRestClient(port);
        }

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = resourceClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("Signed JWT rejected");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-provider.clock.type=fixed")
    @TestPropertySource(properties = "rms.jwt-provider.clock.fixed-datetime=2024-01-01T12:30")
    @Nested
    class InvalidExpTokenTest {

        private TestRestClient resourceClient;

        @BeforeEach
        void beforeEach(@Value("${local.server.port}") int port) throws Exception {
            this.resourceClient = JsonWebTokenValidationTest.this.createRestClient(port);
        }

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = resourceClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("Jwt expired");
        }
    }

    @SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
    @TestPropertySource(properties = "rms.jwt-validator.claim.issuer=dummy")
    @Nested
    class InvalidIssuerTokenTest {

        private TestRestClient resourceClient;

        @BeforeEach
        void beforeEach(@Value("${local.server.port}") int port) throws Exception {
            this.resourceClient = JsonWebTokenValidationTest.this.createRestClient(port);
        }

        @Test
        void tesValidToken(@Autowired JsonWebTokenGenerator generator) {

            String tokenId = generator.generateToken(TEST_USER);
            Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + tokenId);

            ResponseEntity<String> actual = resourceClient.hello(header);

            assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(actual.getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE)).contains("iss claim is not valid");
        }
    }


    // ----------------------------------------------------- setup fixture

    private static final UserClaims TEST_USER = new UserClaims() {

        @Override
        public String getUserId() {
            return "test";
        }

        @Override
        public String getUserPrincipalName() {
            return "test";
        }

        @Override
        public Set<String> getGroups() {
            return Set.of("roleA");
        }
    };

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableWebSecurity(debug = true)
    @Import({ JwtProviderConfiguration.class, JwtValidatorConfiguration.class })
    static class TestConfig {

        @Bean
        AuthorizeRequestCustomizer authorizeRequestCustomizer() {
            // TODO Improve when https://github.com/microsoft/vscode-java-pack/issues/530 is fixed
            return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                    .anyRequest().authenticated();
        }

        @Bean
        TestResource helloResource() {
            return new TestResource();
        }
    }


    // ----------------------------------------------------- util methods

    TestRestClient createRestClient(int port) throws Exception {

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(new NopResponseErrorHandler())
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(TestRestClient.class);
    }


    // ----------------------------------------------------- client side stub interface

    public interface TestRestClient {

        @GetExchange("/hello")
        ResponseEntity<String> hello(@RequestHeader Map<String, ?> headers);
    }


    // ----------------------------------------------------- server side stub classes

    @RestController
    static class TestResource {

        @GetMapping("/hello")
        public String hello() {
            return "ok";
        }
    }
}
