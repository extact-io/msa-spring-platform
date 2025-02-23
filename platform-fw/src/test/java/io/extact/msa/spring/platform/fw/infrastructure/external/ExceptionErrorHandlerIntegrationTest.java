package io.extact.msa.spring.platform.fw.infrastructure.external;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.core.auth.client.BearerTokenExtractor;
import io.extact.msa.spring.platform.core.auth.client.BearerTokenRequestInitializer;
import io.extact.msa.spring.platform.core.auth.client.RmsClientAuthenticationToken;
import io.extact.msa.spring.platform.core.auth.configure.AuthorizeHttpRequestCustomizer;
import io.extact.msa.spring.platform.core.auth.jwt.RmsJwtAuthConfig;
import io.extact.msa.spring.platform.core.condition.EnableAutoConfigurationWithoutJpa;
import io.extact.msa.spring.platform.core.jwt.encode.GenerateToken;
import io.extact.msa.spring.platform.core.jwt.encode.JwtEncodeConfig;
import io.extact.msa.spring.platform.core.jwt.encode.UserClaims;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException;
import io.extact.msa.spring.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.spring.platform.fw.exception.RmsServiceUnavailableException;
import io.extact.msa.spring.platform.fw.exception.RmsSystemException;
import io.extact.msa.spring.platform.fw.exception.RmsValidationException;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorItem;
import io.extact.msa.spring.platform.fw.exception.response.ValidationErrorMessage;
import io.extact.msa.spring.platform.fw.infrastructure.framework.validator.ValidatorConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.ExceptionHandled;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerConfig;
import io.extact.msa.spring.platform.fw.interfaces.webapi.RestControllerExceptionHandler;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields;
import io.extact.msa.spring.platform.fw.stub.server.person.domain.model.EqualPairFields.EqualPairFieldsValidatable;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

/**
 * {@link RestControllerExceptionHandler}と{@link RestClientErrorHandler}の両方を使った
 * Controller -> RestClient間のエラーハンドリングの結合テスト
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ExceptionErrorHandlerIntegrationTest {

    private static final String PARAMETER_ERROR_MESSAGE = "ex.ParameterErrorException.message";
    private static final String CONVERT_ERROR_MESSAGE = "ex.TypeMismatchException.massage";

    @Autowired
    private ExceptionTestClient client;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfigurationWithoutJpa
    @EnableWebSecurity(debug = true)
    @Import({
            RestControllerConfig.class,
            ValidatorConfig.class,
            JwtEncodeConfig.class,
            RmsJwtAuthConfig.class })
    static class TestConfig {

        @Bean
        ExceptionTestController exceptionTestController() {
            return new ExceptionTestController();
        }

        @Bean
        ExceptionTestClient exceptionTestClient(Environment env) {

            RestClient restClient = RestClient.builder()
                    .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                    .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                    .requestInitializer(new BearerTokenRequestInitializer())
                    .build();

            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
            return factory.createClient(ExceptionTestClient.class);
        }

        // ---------- for Spring Security
        @Bean
        AuthorizeHttpRequestCustomizer authorizeRequestCustomizer() {
            return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) -> configurer
                    .requestMatchers("/auth").hasRole("admin")
                    .anyRequest().permitAll();
        }
    }

    @BeforeEach
    void beforeEach() {
        // 直前のテストの状態がThreadLocalに残っているので事前にクリア
        SecurityContextHolder.clearContext();
    }

    @Test
    void occurSecurityConstraint401ExceptionTest() {

        assertThatThrownBy(() -> client.adminApi())
                .isInstanceOfSatisfying(SecurityConstraintException.class, e -> {
                    assertThat(e.getErrorStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                });
    }

    @Test
    void occurSecurityConstraint403ExceptionTest() {

        ResponseEntity<AuthData> response = client.authenticate("1", "member");

        AuthData authData = response.getBody();
        assertThat(authData.userId()).isEqualTo("1");
        assertThat(authData.groups()).isEqualTo(Set.of("member"));

        // クライアント側のログイン関連処理
        String bearerToken = BearerTokenExtractor.extract(response.getHeaders());
        RmsClientAuthenticationToken token = RmsClientAuthenticationToken.builder()
                .userId(authData.userId())
                .bearerToken(bearerToken)
                .groups(authData.groups())
                .build();

        SecurityContextHolder.setContext(new SecurityContextImpl(token));

        assertThatThrownBy(() -> client.adminApi())
                .isInstanceOfSatisfying(SecurityConstraintException.class, e -> {
                    assertThat(e.getErrorStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });
    }

    @Test
    void occurNotFoundBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurNotFoundBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur not found");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
                        });
    }

    @Test
    void occurDuplicateBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurDuplicateBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur duplicate");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.DUPLICATE);
                        });
    }

    @Test
    void occurForbiddenBusinessFlowExceptionTest() {
        assertThatThrownBy(() -> client.occurForbiddenBusinessFlowException())
                .isInstanceOfSatisfying(
                        BusinessFlowException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("occur forbidden");
                            assertThat(e.getCauseType()).isEqualTo(CauseType.FORBIDDEN);
                        });
    }

    @Test
    void occurServiceUnavailableExceptionTest() {
        assertThatThrownBy(() -> client.occurServiceUnavailableException())
                .isInstanceOfSatisfying(
                        RmsServiceUnavailableException.class,
                        e -> {
                            assertThat(e.getMessage()).contains("service unavailable");
                        });
    }

    @Test
    void pathParamSingleTest(@Autowired MessageSource messageSource) {
        // このパターンはキッチリテストを行う
        assertThatThrownBy(() -> client.pathParamSingle("1"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {
                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(1);
                            assertThat(error.validationErrorItems().get(0).fieldName()).isEqualTo("val1");
                            assertThat(error.validationErrorItems().get(0).message()).contains("サイズにしてください");
                        });
    }

    @Test
    void pathParamMultiTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.pathParamMulti("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {
                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(2);
                        });
    }

    @Test
    void queryParamSingleTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.queryParamSingle("1"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(1);
                        });
    }

    @Test
    void queryParamMultiTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.queryParamMulti("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(HandlerMethodValidationException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(2);
                        });
    }

    @Test
    void queryParamDtoTest(@Autowired MessageSource messageSource) {
        // このパターンはキッチリテストを行う
        assertThatThrownBy(() -> client.queryParamDto("1", "2"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(PARAMETER_ERROR_MESSAGE, null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(3);

                            Map<String, String> itemMap = error.validationErrorItems().stream()
                                    .collect(Collectors.toMap(
                                            ValidationErrorItem::fieldName,
                                            ValidationErrorItem::message));
                            assertThat(itemMap).containsKey("値1");
                            assertThat(itemMap.get("値1")).contains("サイズにしてください");
                            assertThat(itemMap).containsKey("値2");
                            assertThat(itemMap.get("値2")).contains("サイズにしてください");
                            assertThat(itemMap).containsKey("入力値");
                            assertThat(itemMap.get("入力値")).contains("同じ値にしてください");
                        });
    }

    @Test
    void bodyParamDtoTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.bodyParamDto(new ParamDto("1", "2")))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();
                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(3);
                        });
    }

    @Test
    void convertedTypePathParamTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.convertedTypePathParam("a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);

                            String fieldErrorMessage = messageSource.getMessage(
                                    CONVERT_ERROR_MESSAGE,
                                    new Object[] { int.class.getSimpleName() },
                                    Locale.getDefault());

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(errorMessage);
                            assertThat(error.validationErrorItems()).hasSize(1);
                            assertThat(error.validationErrorItems().get(0).fieldName()).isEqualTo("val1");
                            assertThat(error.validationErrorItems().get(0).message()).contains(fieldErrorMessage);
                        });
    }

    @Test
    void convertedTypeQueryParamTest(@Autowired MessageSource messageSource) {
        assertThatThrownBy(() -> client.convertedTypeQueryParam("a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(errorMessage);
                            assertThat(error.validationErrorItems()).hasSize(1);
                        });
    }

    @Test
    void queryParamDtoWithRequestParam1Test(@Autowired MessageSource messageSource) {
        // MethodArgumentNotValidExceptionになる(ParamDto格納エラでコンバートエラーまで行かず)
        assertThatThrownBy(() -> client.queryParamDtoWithRequestParam("1", "2", "a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String message = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(message);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentNotValidException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(message);
                            assertThat(error.validationErrorItems()).hasSize(3);
                        });
    }

    @Test
    void queryParamDtoWithRequestParam2Test(@Autowired MessageSource messageSource) {
        // MethodArgumentTypeMismatchExceptionになる(ParamDto格納成功でコンバートエラー)
        assertThatThrownBy(() -> client.queryParamDtoWithRequestParam("123", "123", "a"))
                .isInstanceOfSatisfying(
                        RmsValidationException.class,
                        e -> {

                            String errorMessage = messageSource.getMessage(
                                    PARAMETER_ERROR_MESSAGE,
                                    null,
                                    Locale.getDefault());
                            assertThat(e.getMessage()).startsWith(errorMessage);

                            ValidationErrorMessage error = e.getErrorMessage();

                            assertThat(error).isNotNull();
                            assertThat(error.errorReason())
                                    .isEqualTo(MethodArgumentTypeMismatchException.class.getSimpleName());
                            assertThat(error.errorMessage()).isEqualTo(errorMessage);
                            assertThat(error.validationErrorItems()).hasSize(1);
                        });
    }

    @Test
    void occurSystemExceptionTest() {
        assertThatThrownBy(() -> client.occurSystemException())
                .isInstanceOfSatisfying(
                        RmsSystemException.class,
                        e -> assertThat(e.getMessage()).contains("occur system error"));
    }

    @Test
    void occurUnknowExceptionTest() {
        assertThatThrownBy(() -> client.occurUnknowException())
                .isInstanceOfSatisfying(
                        RmsSystemException.class,
                        e -> assertThat(e.getMessage()).contains("json deserialize error"));
    }

    @HttpExchange
    static interface ExceptionTestClient {

        @GetExchange("/login") // 認証なし
        ResponseEntity<AuthData> authenticate(@RequestParam("loginId") String loginId,
                @RequestParam("password") String password);

        @GetExchange("/auth") // 認証あり(admin-role)
        boolean adminApi();

        @GetExchange("/pathParamSingle/{val1}")
        void pathParamSingle(@PathVariable String val1);

        @GetExchange("/pathParamMulti/{val1}/{val2}")
        void pathParamMulti(@PathVariable String val1, @PathVariable String val2);

        @GetExchange("/queryParamSingle")
        void queryParamSingle(@RequestParam String val1);

        @GetExchange("/queryParamMulti")
        void queryParamMulti(@RequestParam String val1, @RequestParam String val2);

        @GetExchange("/queryParamDto")
        void queryParamDto(@RequestParam String val1, @RequestParam String val2);

        @PostExchange("/bodyParamDto")
        void bodyParamDto(@Validated @RequestBody ParamDto dto);

        @GetExchange("/convertedTypePathParam/{val1}")
        void convertedTypePathParam(@PathVariable String val1);

        @GetExchange("/convertedTypeQueryParam")
        void convertedTypeQueryParam(@RequestParam String val1);

        @GetExchange("/queryParamDtoWithRequestParam")
        void queryParamDtoWithRequestParam(
                @RequestParam String val1,
                @RequestParam String val2,
                @RequestParam String val3);

        @GetExchange("/occurNotFoundBusinessFlowException")
        void occurNotFoundBusinessFlowException();

        @GetExchange("/occurDuplicateBusinessFlowException")
        void occurDuplicateBusinessFlowException();

        @GetExchange("/occurForbiddenBusinessFlowException")
        void occurForbiddenBusinessFlowException();

        @GetExchange("/occurServiceUnavailableException")
        void occurServiceUnavailableException();

        @GetExchange("/occurSystemException")
        void occurSystemException();

        @GetExchange("/occurUnknownException")
        void occurUnknowException();
    }

    record AuthData(
            String userId,
            Set<String> groups) implements UserClaims {

        public String principalName() {
            return this.userId + "@msa-rms";
        }
    }

    @EqualPairFields
    static record ParamDto(
            @Size(min = 2) String val1,
            @Size(min = 2) String val2) implements EqualPairFieldsValidatable {

        @Override
        public String getPair1() {
            return val1;
        }

        @Override
        public String getPair2() {
            return val2;
        }
    }

    @RestController
    @ExceptionHandled
    public static class ExceptionTestController {

        @GetMapping("/login") // 認証なし
        @GenerateToken
        public AuthData authenticate(@RequestParam("loginId") String loginId,
                @RequestParam("password") String password) {
            return new AuthData(loginId, Set.of(password));
        }

        @GetMapping("/auth") // 認証あり(admin-role)
        public boolean adminApi() {
            return true;
        }

        @GetMapping("/pathParamSingle/{val1}")
        public void pathParamSingle(@PathVariable @Size(min = 2) String val1) {
            // NOP
        }

        @GetMapping("/pathParamMulti/{val1}/{val2}")
        public void pathParamMulti(@PathVariable @Size(min = 2) String val1, @PathVariable @Size(min = 2) String val2) {
            // NOP
        }

        @GetMapping("/queryParamSingle")
        public void queryParamSingle(@RequestParam @Size(min = 2) String val1) {
            // NOP
        }

        @GetMapping("/queryParamMulti")
        public void queryParamMulti(@RequestParam @Size(min = 2) String val1,
                @RequestParam @Size(min = 2) String val2) {
            // NOP
        }

        @GetMapping("/queryParamDto")
        public void queryParamDto(@Validated ParamDto dto) {
            // NOP
        }

        @PostMapping("/bodyParamDto")
        public void bodyParamDto(@Validated @RequestBody ParamDto dto) {
            // NOP
        }

        @GetMapping("/convertedTypePathParam/{val1}")
        public void convertedTypePathParam(@PathVariable int val1) {
            // NOP
        }

        @GetMapping("/convertedTypeQueryParam")
        public void convertedTypeQueryParam(@RequestParam int val1) {
            // NOP
        }

        @GetMapping("/queryParamDtoWithRequestParam")
        public void queryParamDtoWithRequestParam(@Validated ParamDto dto, @RequestParam int val3) {
            // NOP
        }

        @GetMapping("/occurNotFoundBusinessFlowException")
        public void occurNotFoundBusinessFlowException() {
            throw new BusinessFlowException("occur not found.", CauseType.NOT_FOUND);
        }

        @GetMapping("/occurDuplicateBusinessFlowException")
        public void occurDuplicateBusinessFlowException() {
            throw new BusinessFlowException("occur duplicate.", CauseType.DUPLICATE);
        }

        @GetMapping("/occurForbiddenBusinessFlowException")
        public void occurForbiddenBusinessFlowException() {
            throw new BusinessFlowException("occur forbidden.", CauseType.FORBIDDEN);
        }

        @GetMapping("/occurServiceUnavailableException")
        public void occurServiceUnavailableException() {
            throw new RmsServiceUnavailableException("service unavailable.");
        }

        @GetMapping("/occurSystemException")
        public void occurSystemException() {
            throw new RmsSystemException("occur system error.");
        }

        @GetMapping("/occurUnknownException")
        public ResponseEntity<Object> occurUnknownException() {
            Exception occuredException = new IllegalStateException("unknown exception.");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("rms-exception", occuredException.getClass().getSimpleName())
                    .body(occuredException.getMessage());
        }
    }

}
