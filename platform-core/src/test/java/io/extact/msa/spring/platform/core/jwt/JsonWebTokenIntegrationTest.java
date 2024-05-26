package io.extact.msa.spring.platform.core.jwt;

import static io.extact.msa.spring.platform.core.jwt.JsonWebTokenIntegrationTest.LoginRestClient.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
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

import io.extact.msa.spring.platform.core.jwt.provider.GenerateToken;
import io.extact.msa.spring.platform.core.jwt.provider.UserClaims;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderConfiguration;
import io.extact.msa.spring.platform.core.jwt.provider.config.JwtProviderProperties;
import io.extact.msa.spring.platform.core.jwt.validator.AuthorizeRequestCustomizer;
import io.extact.msa.spring.platform.core.jwt.validator.JwtValidatorConfiguration;
import io.extact.msa.spring.platform.core.testlib.NopResponseErrorHandler;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class JsonWebTokenIntegrationTest {

    private LoginRestClient loginClient;
    private TestRestClient resourceClient;

    private static Authentication actualAuthenticationOnServerSide;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableWebSecurity(debug = true)
    @Import({ JwtProviderConfiguration.class, JwtValidatorConfiguration.class })
    static class TestConfig {

        @Bean
        AuthorizeRequestCustomizer authorizeRequestCustomizer() {

            // TODO Improve when https://github.com/microsoft/vscode-java-pack/issues/530 is fixed
            return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/role/a").hasRole("roleA")
                    .requestMatchers("/role/c/**").hasRole("roleC")
                    .requestMatchers("/check").hasRole("roleCheck")
                    .anyRequest().authenticated();
        }

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
        assertThat(roles).containsExactlyInAnyOrder("ROLE_roleA", "ROLE_roleB");

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

    @Test
    void testAuthorization() {

        // response assertion
        ResponseEntity<TestUserClaims> response = loginClient.login(SUCCESS);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        String tokenId = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, tokenId);

        // check roles
        ResponseEntity<String> actual = resourceClient.roleA(header);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

        actual = resourceClient.roleC(header);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        actual = resourceClient.roleCc(header);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        actual = resourceClient.everyone(header);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testCheck() {

        // response assertion
        ResponseEntity<TestUserClaims> response = loginClient.login(SUCCESS);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // BAD_REQUEST(バリデーションエラー)ではなく認証エラーが返ってくること
        ResponseEntity<String> actual = resourceClient.check(Collections.emptyMap(), "");
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // BAD_REQUEST(バリデーションエラー)ではなく認可エラーが返ってくること
        String tokenId = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, tokenId);
        actual = resourceClient.check(header, "");
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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

        @GetExchange("/role/a")
        ResponseEntity<String> roleA(@RequestHeader Map<String, ?> headers);

        @GetExchange("/role/c")
        ResponseEntity<String> roleC(@RequestHeader Map<String, ?> headers);

        @GetExchange("/role/c/c")
        ResponseEntity<String> roleCc(@RequestHeader Map<String, ?> headers);

        @GetExchange("/everyone")
        ResponseEntity<String> everyone(@RequestHeader Map<String, ?> headers);

        @GetExchange("/check")
        ResponseEntity<String> check(@RequestHeader Map<String, ?> headers, @RequestParam("val") String val);
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

        @GetMapping("/role/a")
        public String roleA() {
            return "ok";
        }

        @GetMapping("/role/c")
        public String roleC() {
            return "ok";
        }

        @GetMapping("/role/c/c")
        public String roleCc() {
            return "ok";
        }

        @GetMapping("/everyone")
        public String everyone() {
            return "ok";
        }

        @GetMapping("/check")
        public String check(@RequestParam("val") @NotBlank String val) {
            return "ok";
        }
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    static @interface ExceptionMapping {
    }

    @RestControllerAdvice(annotations = ExceptionMapping.class)
    static class ResourceExceptionMapper extends ResponseEntityExceptionHandler {

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Void> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Void> handleErrorException(Exception ex, WebRequest request) {
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
