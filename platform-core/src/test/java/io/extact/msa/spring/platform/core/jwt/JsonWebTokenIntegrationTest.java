package io.extact.msa.spring.platform.core.jwt;

import static io.extact.msa.spring.platform.core.jwt.JsonWebTokenIntegrationTest.LoginRestClient.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.qos.logback.access.tomcat.LogbackValve;
import io.extact.msa.spring.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.spring.platform.core.jwt.provider.JsonWebTokenGenerator;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProvideResponseAdvice;
import io.extact.msa.spring.platform.core.jwt.provider.JwtProviderProperties;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.testlib.NopResponseErrorHandler;
import lombok.Data;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JsonWebTokenIntegrationTest {

    private LoginRestClient loginClient;
    private TestRestClient resourceClient;

    private static Authentication actualAuthenticationOnServerSide;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableWebSecurity(debug = true)
    @Import(JwtProviderConfiguration.class)
    static class TestConfig {

        @Bean
        JwtProvideResponseAdvice jwtProvideResponseAdvice(JsonWebTokenGenerator generator) {
            return new JwtProvideResponseAdvice(generator);
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/login").permitAll()
                            .anyRequest().authenticated())
                    .csrf((csrf) -> csrf.disable())
                    .oauth2ResourceServer((oauth2) -> oauth2
                            .jwt(jwt -> jwt
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                    .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling((exceptions) -> exceptions
                            .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                            .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
            return http.build();
        }

        // ----------------------------------- for test facilities

        JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthoritiesClaimName("groups");

            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
            return jwtAuthenticationConverter;
        }

        @Bean
        JwtDecoder jwtDecoder(@Value("${jwt.public.key}") RSAPublicKey key) {
            return NimbusJwtDecoder.withPublicKey(key).build();
        }

        @Bean
        TomcatServletWebServerFactory servletContainer() {
            TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
            LogbackValve valve = new LogbackValve();
            valve.setFilename(LogbackValve.DEFAULT_FILENAME);
            tomcatServletWebServerFactory.addContextValves(valve);
            return tomcatServletWebServerFactory;
        }

        // ----------------------------------- for test stub beans

        @Bean
        LoginResource testLoginResource() {
            return new LoginResource();
        }

        @Bean
        TestResource helloResource() {
            return new TestResource();
        }

        @Bean
        ResourceExceptionMapper resourceExceptionMapper() {
            return new ResourceExceptionMapper();
        }
    }


    // ----------------------------------------------------- lifecycle methods

    @BeforeEach
    void beforeEach(@Value("${local.server.port}") int port) throws Exception {

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(new NopResponseErrorHandler())
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        this.loginClient = factory.createClient(LoginRestClient.class);
        this.resourceClient = factory.createClient(TestRestClient.class);
    }

    @AfterEach
    void afterEach() {
        actualAuthenticationOnServerSide = null;
    }

    // ----------------------------------------------------- test methods

    @Test
    void testGenerateTokenOnSuccess() {

        ResponseEntity<TestUserClaims> response = loginClient.login(SUCCESS);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(TestUserClaims.DEFAULT_INSTANCE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isNotBlank();
    }

    @Test
    void testGenerateTokenOnError() {

        ResponseEntity<TestUserClaims> response = loginClient.login(ERROR);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
    }

    @Test
    void testAuthenticateOnSuccess(@Autowired JwtProviderProperties properties) {

        // response assertion
        ResponseEntity<TestUserClaims> response = loginClient.login(SUCCESS);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tokenId = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, tokenId);

        ResponseEntity<String> actual = resourceClient.hello(header);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo("Hello, " + TestUserClaims.DEFAULT_INSTANCE.getUserId() + "!");

        // authentication assertion
        Authentication auth = actualAuthenticationOnServerSide;
        assertThat(auth.getName()).isEqualTo("test");

        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        assertThat(roles).containsExactlyInAnyOrder("SCOPE_roleA", "SCOPE_roleB");

        // jwt assertion
        assertThat(auth.getCredentials()).isInstanceOf(Jwt.class);
        Jwt jwt = (Jwt) auth.getCredentials();

        assertThat(jwt.getClaimAsString("sub")).isEqualTo("test");
        assertThat(jwt.getClaimAsString("upn")).isEqualTo("test@test");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo(properties.getClaim().getIssuer());
        assertThat(jwt.getClaimAsStringList("groups")).containsExactlyInAnyOrder("roleA", "roleB");
        assertThat(jwt.getClaimAsInstant("exp")).isNotNull();
        assertThat(jwt.getClaimAsInstant("iat")).isNotNull();
        assertThat(jwt.getClaimAsString("jti")).isNotNull();
    }

    @Test
    void testAuthenticateOnError() {

        ResponseEntity<String> actual = resourceClient.hello(Collections.emptyMap());
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ----------------------------------------------------- client side stub interface

    public interface LoginRestClient {

        static final String SUCCESS = "success";
        static final String ERROR = "error";

        @GetExchange("/login")
        ResponseEntity<TestUserClaims> login(@RequestParam("pttn") String pttn);
    }

    public interface TestRestClient {

        @GetExchange("/hello")
        ResponseEntity<String> hello(@RequestHeader Map<String, ?> headers);
    }


    // ----------------------------------------------------- server side stub classes

    @RestController
    @ExceptionMapping
    static class LoginResource {

        @GetMapping("/login")
        @GenerateToken
        public TestUserClaims login(@RequestParam("pttn") String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return TestUserClaims.DEFAULT_INSTANCE;
        }
    }

    @RestController
    @ExceptionMapping
    static class TestResource {

        @GetMapping("/hello")
        public String hello(Authentication authentication) {
            actualAuthenticationOnServerSide = authentication;
            return "Hello, " + authentication.getName() + "!";
        }
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    static @interface ExceptionMapping {
    }

    @RestControllerAdvice(annotations = ExceptionMapping.class)
    static class ResourceExceptionMapper extends ResponseEntityExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Void> handleNotFoundException(Exception ex, WebRequest request) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Data
    public static class TestUserClaims implements UserClaims {

        static final TestUserClaims DEFAULT_INSTANCE;
        static {
            DEFAULT_INSTANCE = new TestUserClaims();
            DEFAULT_INSTANCE.setUserId("test");
            DEFAULT_INSTANCE.setUserPrincipalName("test@test");
            DEFAULT_INSTANCE.setGroups(Set.of("roleA", "roleB"));
        }

        private String userId;
        private String userPrincipalName;
        private Set<String> groups;
    }
}
